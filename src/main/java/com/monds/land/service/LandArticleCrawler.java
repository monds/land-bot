package com.monds.land.service;

import com.monds.land.common.DescriptionUtils;
import com.monds.land.domain.LandArticle;
import com.monds.land.domain.LandRegion;
import com.monds.land.dto.ArticleDetailResponse;
import com.monds.land.dto.ArticlesResponse;
import com.monds.land.repository.LandArticleRepository;
import com.monds.land.repository.LandRegionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class LandArticleCrawler {
    private final LandArticleRepository articleRepository;
    private final LandRegionRepository regionRepository;
    private final NaverLandApi landApi;
    private final MessageSender messageSender;

    private final String[] TAG_STOP_WORDS = {"25년이내", "25년이상", "융자금적은", "2년이내"};

    private Map<String, String> createQueryMap(String cortarNo, int page) {
        Map<String, String> query = new HashMap<>();
        query.put("cortarNo", cortarNo);
        query.put("order", "dateDesc");
        query.put("realEstateType", "APT:OPST:ABYG:OBYG:GM:OR:VL:DDDGG:JWJT:SGJT:HOJT");
        query.put("tradeType", "B1");
        query.put("priceMin", "15000");
        query.put("priceMax", "20000");
        query.put("showArticle", "false");
        query.put("sameAddressGroup", "false");
        query.put("priceType", "RETAIL");
        query.put("page", String.valueOf(page));
        return query;
    }

    @Scheduled(fixedDelay = 1000L * 60 * 10)
    public void crawlLandArticles() throws Exception {

        final int due = 0; // 어제까지 수집
        LocalDate baseDate = LocalDate.now().minusDays(due); // yyyy-mm-dd

        List<LandRegion> regions = regionRepository.findAll();

        for (LandRegion region : regions) {
            getArticlesByRegion(baseDate, region);
        }
    }

    public void getArticlesByRegion(LocalDate baseDate, LandRegion region) throws IOException {
        String regionId = region.getId();
        // 설정된 날짜까지만 수집 (우선은 -2일)
        // 이미 등록된 id 라면 즉시 종료
        // 모든 article 을 수집했다면 페이지 넘버 증가시켜서 수집
        boolean runnable = true;
        int page = 1;
        int count = 0;
        while (runnable) {

            ArticlesResponse articles = landApi.getArticles(createQueryMap(regionId, page++))
                .execute()
                .body();

            if (articles.getArticleList().isEmpty()) {
                break;
            }

            List<LandArticle> landArticles = new LinkedList<>();
            articleLoop:
            for (ArticlesResponse.Article article : articles.getArticleList()) {
                // 과거 날짜이거나 이미 적재된 데이터면 현재 지역을 종료한다.
                if (article.invalid(baseDate) || articleRepository.existsById(Integer.parseInt(article.getArticleNo()))) {
                    runnable = false;
                    break;
                }

                // 지하는 제외
                if (article.isBasementFloor()) continue;

                // 가끔 제목에 전세자금대출불가 넣기도 함
                String articleFeatureDesc = article.getArticleFeatureDesc();
                if (DescriptionUtils.unableLoan(articleFeatureDesc)) {
                    continue;
                }

                ArticleDetailResponse articleDetail = landApi.getArticleDetailById(Integer.parseInt(article.getArticleNo()))
                    .execute()
                    .body();

                for (String stopWord : TAG_STOP_WORDS) {
                    if (articleDetail.getArticleDetail().getTagList().contains(stopWord)) continue articleLoop;
                }

                // 전세대출불가 내용 존재 시 제외
                String detailDescription = articleDetail.getArticleDetail().getDetailDescription();
                if (DescriptionUtils.unableLoan(detailDescription) || DescriptionUtils.firstMoveIn(detailDescription)) {
                    continue;
                }

                LandArticle landArticle = articleDetail.toLandArticle(regionId, article);

                if (DescriptionUtils.noMortgage(detailDescription)) {
                    landArticle.setNoloan(true);
                }

                // 융자금이 없는 집인지 알 수 없는데 사진이 없다면 보지 않는다.
                if (!landArticle.isNoloan() && !articleDetail.hasPhotos()) {
                    continue;
                }

                messageSender.send(landArticle);

                landArticles.add(landArticle);
                count++;
            }

            articleRepository.saveAll(landArticles);
        }

        log.info("[네이버] {} count: {}", region.getAddressFullName(), count);
    }
}
