package com.monds.land.service;

import com.monds.land.common.DescriptionUtils;
import com.monds.land.domain.ZigbangAddress;
import com.monds.land.domain.ZigbangItem;
import com.monds.land.domain.ZigbangSubway;
import com.monds.land.dto.ZigbangItemIdsResponse;
import com.monds.land.dto.ZigbangItemResponse;
import com.monds.land.dto.ZigbangItemsListRequest;
import com.monds.land.dto.ZigbangItemsListResponse;
import com.monds.land.repository.ZigbangAddressRepository;
import com.monds.land.repository.ZigbangItemRepository;
import com.monds.land.repository.ZigbangSubwayRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.time.temporal.TemporalAdjusters.firstDayOfYear;

@Slf4j
@RequiredArgsConstructor
@Service
public class ZigbangCrawler {
    private final ZigbangApi zigbangApi;
    private final ZigbangSubwayRepository subwayRepository;
    private final ZigbangItemRepository itemRepository;
    private final ZigbangAddressRepository addressRepository;
    private final MessageSender messageSender;

    @Scheduled(fixedDelay = 1000L * 60 * 10)
    public void findRooms() throws IOException {
        List<ZigbangSubway> subways = subwayRepository.getAllByEnableTrue();

        for (ZigbangSubway subway : subways) {

            // 지하철역 주변 방 ID 조회
            ZigbangItemIdsResponse body = zigbangApi.getItemIdsBySubwayId(subway.getId(), createQuery())
                .execute()
                .body();

            List<Integer> itemIds = body.getListItems()
                .stream()
                .map(listItem -> {
                    ZigbangItemIdsResponse.ListItem.SimpleItem simpleItem = listItem.getSimpleItem();
                    return simpleItem == null ? null : listItem.getSimpleItem().getItemId();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

            log.info("[직방] {} count: {}", subway.getName(), body.getTotalCount());

            if (itemIds.isEmpty()) {
                continue;
            }

            // 조회된 방 조회 (정렬된 리스트를 돌려준다.)
            ZigbangItemsListResponse itemsList = zigbangApi.getItemsList(new ZigbangItemsListRequest(itemIds))
                .execute()
                .body();

            for (ZigbangItemsListResponse.Item item : itemsList.getItems()) {
                // 이미 체크한 방이면 skip
                if (itemRepository.existsById(item.getItemId())) {
                    continue;
                }

                ZigbangItemResponse itemResponse = zigbangApi.getItemById(item.getItemId()).execute().body();

                itemRepository.save(new ZigbangItem(item.getItemId()));

                // 이미 체크한 주소면 skip (중복 주소 방지)
                String address = itemResponse.getAddress() + "/" + itemResponse.getItem().getFloorString();
                if (addressRepository.existsByAddressEquals(address)) {
                    continue;
                }

                addressRepository.save(new ZigbangAddress(item.getItemId(), address));

                if (!itemResponse.getItem().getCreatedAt().toLocalDate().equals(LocalDate.now())) {
                    continue;
                }

                String description = itemResponse.getDescription();

                // 내용에 첫입주, 대출불가 키워드가 존재하는지 체크
                if (DescriptionUtils.unableLoan(description) || DescriptionUtils.firstMoveIn(description)) {
                    continue;
                }

                // 준공년도 체크
                LocalDate approveDate = itemResponse.getApproveDate();
                YearMonth yearMonth = parseYearMonth(itemResponse.getItem().getApproveDate());
                Year year = parseYear(itemResponse.getItem().getApproveDate());
                if (itemResponse.getItem().getApproveDate().contains("사용승인전")
                    || (approveDate != null && approveDate.isBefore(LocalDate.now().minusYears(10)))
                    || (yearMonth != null && (yearMonth.isBefore(YearMonth.now().minusYears(10)) || yearMonth.getYear() == Year.now().getValue()))
                    || (year != null && (year.isBefore(Year.now().minusYears(10)) || year.equals(Year.now())))) {
                    continue;
                }

                // 2년 내의 신축급일 경우에는 융자금이 없는 경우만 본다.
                if (approveDate != null
                    && approveDate.isAfter(LocalDate.now().minusYears(2).with(firstDayOfYear()))
                    && itemResponse.hasMortgage()) {
                    continue;
                }

                messageSender.send(itemResponse);
            }
        }
    }

    private YearMonth parseYearMonth(String approveDate) {
        try {
            return YearMonth.parse(approveDate, DateTimeFormatter.ofPattern("[yyyy.MM][yy.MM][yyyy년M월][yyyy년 M월]"));
        } catch (Exception e) {
            return null;
        }
    }

    private Year parseYear(String approveDate) {
        try {
            return Year.parse(approveDate);
        } catch (Exception e) {
            return null;
        }
    }

    private Map<String, String> createQuery() {
        Map<String, String> query = new HashMap<>();
        query.put("deposit_s", "15000");
        query.put("deposit_e", "20000");
        query.put("detail", "false");
        query.put("domain", "zigbang");
        query.put("floor", "1~");
        query.put("radius", "1");
        query.put("rent_s", "0");
        query.put("sales_type", "yearRent");
        return query;
    }
}
