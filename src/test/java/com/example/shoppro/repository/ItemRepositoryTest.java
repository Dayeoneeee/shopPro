package com.example.shoppro.repository;

import com.example.shoppro.constant.ItemSellstatus;
import com.example.shoppro.entity.Item;
import com.example.shoppro.entity.QItem;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.query.JpaQueryMethodFactory;

import java.nio.channels.Pipe;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Test
    @DisplayName("양방향 테스트")
    public void selectItem(){

        //필요한 값 : 부모 id 411L
        //실행 내용 : 부모를 item을 검색한다. 특정 pk값을 가지고
        Item item =
                itemRepository.findById(411L).get();

        // 결과 예상 : 부모를 검색하면 부모와 + 자식의 모든 데이터를 받는다.

        log.info(item);
        log.info("아이템 명" + item.getItemNm());
        log.info("아이템 이미지" + item.getItemImgList().get(0).getImgUrl());


    }

    @Test
    @DisplayName("상품 저장 테스트")
    public void createItemTest(){

        for (int i = 0; i<200; i++){
            Item item =
                    Item.builder()
                            .itemNm("테스트 상품")
                            .price(10000)
                            .itemDetail("테스트상품 상세설명")
                            .itemSellstatus(ItemSellstatus.SELL)
                            .stockNumber(100)
//                            .regTime(LocalDateTime.now())
//                            .updateTime(LocalDateTime.now())
                            .build();

            item.setItemNm(item.getItemNm() + i);
            item.setItemDetail(item.getItemDetail() + i);
            item.setPrice(item.getPrice() + i);

            Item item1 =
                itemRepository.save(item);
            log.info(item1);

        }

    }

    @Test
    @DisplayName("제품명으로 검색. 2가지에서 다시 2가지 검색")
    public void findByItemNmTest(){

        List<Item> itemListA =
                itemRepository.findByItemNm("테스트상품1");

        itemListA.forEach(item -> log.info(item));
        System.out.println("--------------------------");

        itemListA =
                itemRepository.selectwhereItemNm("테스트상품2");
        itemListA.forEach(item -> log.info(item));
        System.out.println("--------------------------");

        itemListA =
                itemRepository.findByItemNmContaining("테스트");
        itemListA.forEach(item -> log.info(item));
        System.out.println("--------------------------");

        itemListA =
                itemRepository.selectWItemNmLike("1");
        itemListA.forEach(item -> log.info(item));
        System.out.println("--------------------------");

    }

    @Test
    public void priceSearchtest(){
        //가격 검색 테스트
        //사용자가 검색창에 혹은
        //검색이 가능하도록 만들어 놓은 곳에 값을 입력한다.
        //이 조건에 부합하는 아이템 리스트 검색
        Integer price = 10020;

        List<Item> itemList =
        itemRepository.findByPriceLessThan(price);
        for (Item item : itemList) {
            log.info(item);
            log.info("상 품 명 : " + item.getItemNm());
            log.info("상 품 가 격 : " + item.getPrice());
            log.info("상 품 상세설명 : " + item.getItemDetail());
        }

        List<Item> itemListA =
                itemRepository.findByPriceGreaterThan(price);
        for (Item item : itemListA) {
            log.info(item);
            log.info("상 품 명 : " + item.getItemNm());
            log.info("상 품 가 격 : " + item.getPrice());
            log.info("상 품 상세설명 : " + item.getItemDetail());
        }

        List<Item> itemListB =
                itemRepository.findByPriceGreaterThanOrderByPriceAsc(price);
        for (Item item : itemListB) {
            log.info(item);
            log.info("상 품 명 : " + item.getItemNm());
            log.info("상 품 가 격 : " + item.getPrice());
            log.info("상 품 상세설명 : " + item.getItemDetail());
        }
    }

    @Test
    @DisplayName("페이징 추가까지")
    public void findByPriceGreaterThanEqualTest(){
        Pageable pageable = PageRequest
                .of(0,5, Sort.by("id").ascending());
                                                //sort.by의 정렬조건은 entity의 변수명이다(Repository의 34번째 줄)
        Integer price = 10020;
        List<Item> itemList =
        itemRepository.findByPriceGreaterThanEqual(price, pageable);

        itemList.forEach(item -> log.info(item));
    }

    //서비스

    @Test
    public void nativeQuerTest(){

        Pageable pageable =
                PageRequest
                        .of(0,5,Sort.by("price").descending());

        String itemNm = "테스트 상품1";
        List<Item> itemList =
        itemRepository.nativeQuerySelectwhereNamelike(itemNm, pageable);
        itemList.forEach(item -> log.info(item));

    }

    @Test
    public void queryDslTestA(){

        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);

        QItem qItem = QItem.item;
        //select * from item

        String keyword = null;
        ItemSellstatus itemSellstatus = ItemSellstatus.SELL;
        JPAQuery<Item> query=
                queryFactory.selectFrom(qItem)
                        .where(qItem.itemSellstatus.eq(ItemSellstatus.SELL))
                        .where(qItem.itemDetail.like("%"+keyword+"1"+"%"))
                        .orderBy(qItem.price.desc());

        List<Item> itemList = query.fetch();

        for (Item item : itemList){
            System.out.println(item.getItemNm());
        }

    }

    @Test
    public void queryDslTestB(){
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);

        QItem qItem = QItem.item;
        //select * from item
        //상품 검색 조건 입력
        String keyword = "1";
        ItemSellstatus itemSellstatus = ItemSellstatus.SELL;

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if(keyword != null){
            booleanBuilder.or(qItem.itemDetail.like("%"+keyword+"%"));
        }
        if (itemSellstatus != null){
            if (itemSellstatus == ItemSellstatus.SELL){
                booleanBuilder.or(qItem.itemSellstatus.eq(ItemSellstatus.SELL));
            }else {
                booleanBuilder.or(qItem.itemSellstatus.eq(ItemSellstatus.SOLD_OUT));
            }
        }

        JPAQuery<Item> query =
                queryFactory.selectFrom(qItem)
                            .where(booleanBuilder)
                            .orderBy(qItem.price.desc());

//        JPAQuery<Item> query=
//                queryFactory.selectFrom(qItem)
//                        .where(qItem.itemSellstatus.eq(ItemSellstatus.SELL))
//                        .where(qItem.itemDetail.like("%"+keyword+"1"+"%"))
//                        .orderBy(qItem.price.desc());

        List<Item> itemList = query.fetch();

        for (Item item : itemList){
            System.out.println(item.getItemNm());
        }
    }


}