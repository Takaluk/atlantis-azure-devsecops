insert into content.keyword(id, word, description) values
    (1,'마진','수익성 개선 흐름과 가격전가 가능성에 주목'),
    (2,'가이던스','향후 분기 실적 전망과 경영진 코멘트'),
    (3,'신제품','신규 라인업 출시 일정과 반응'),
    (4,'공급망','부품 수급과 생산 안정성'),
    (5,'규제리스크','정책 변화에 따른 비용/제약 가능성'),
    (6,'AI','AI 수요 확대와 응용 범위'),
    (7,'클라우드','클라우드 매출 성장과 고객 확장'),
    (8,'데이터센터','데이터센터 투자와 수요 추이'),
    (9,'반도체','칩 수요 사이클과 가격 동향'),
    (10,'성장둔화','매출 성장 둔화 가능성')
    on conflict do nothing;

insert into content.article(id, title, meta, description, published_at) values
    (1,'애플, 신규 라인업 공개','Bloomberg','신제품 공개와 가격 정책 이슈', '2024-06-12'),
    (2,'아이폰 판매 둔화 우려','Reuters','수요 둔화와 재고 조정 이슈', '2024-05-28'),
    (3,'MS, 클라우드 성장률 재가속','WSJ','엔터프라이즈 수요 회복 신호', '2024-06-10'),
    (4,'MS AI 파트너십 확대','CNBC','AI 서비스 확장과 수익화 전략', '2024-06-03'),
    (5,'엔비디아, 데이터센터 수요 강세','FT','AI 서버 투자 확대 영향', '2024-06-11'),
    (6,'GPU 공급망 병목 재현 가능성','Nikkei','부품 수급 불확실성 재부각', '2024-05-30'),
    (7,'애플, 프리뷰 이벤트 일정 공개','TechCrunch','행사 일정과 기대 포인트 정리', '2024-05-15'),
    (8,'애플 실적 발표, 서비스 매출 주목','MarketWatch','서비스 성장과 마진 개선 포인트', '2024-05-25'),
    (9,'애플 공급망 비용 상승 우려','WSJ','부품 가격 상승과 공급 안정성', '2024-06-01'),
    (10,'MS 클라우드 대형 계약 확대','Bloomberg','대기업 계약 수주 소식', '2024-05-20'),
    (11,'MS AI 파트너십 신규 발표','Reuters','파트너사 확대와 제품 로드맵', '2024-06-05'),
    (12,'엔비디아 데이터센터 수요 급증','CNBC','AI 서버 수요 증가 분석', '2024-05-22'),
    (13,'엔비디아 GPU 공급 일정 지연','Nikkei','납기 지연과 대응 전략', '2024-05-31'),
    (14,'엔비디아 실적 서프라이즈','FT','예상 상회 실적과 가이던스', '2024-06-07')
    on conflict do nothing;

insert into content.stock_keyword(id, stock_id, keyword_id, score) values
    (1,1,1,72),
    (2,1,3,68),
    (3,1,4,55),
    (4,1,5,40),
    (5,2,2,70),
    (6,2,6,78),
    (7,2,7,74),
    (8,3,8,82),
    (9,3,9,80),
    (10,3,1,62)
    on conflict do nothing;

insert into content.stock_article(id, stock_id, article_id) values
    (1,1,1),
    (2,1,2),
    (3,2,3),
    (4,2,4),
    (5,3,5),
    (6,3,6)
    on conflict do nothing;

insert into content.price_event(id, stock_id, title, type, start_date, end_date) values
    (1101,1,'신제품 프리뷰','NEWS','2024-05-14','2024-05-18'),
    (1102,1,'실적 발표','EARNINGS','2024-05-24','2024-05-27'),
    (1103,1,'공급망 이슈','SUPPLY','2024-05-31','2024-06-03'),
    (1201,2,'클라우드 성장','NEWS','2024-05-18','2024-05-21'),
    (1202,2,'AI 파트너십','NEWS','2024-06-04','2024-06-07'),
    (1301,3,'데이터센터 수요','NEWS','2024-05-20','2024-05-23'),
    (1302,3,'GPU 공급망','SUPPLY','2024-05-30','2024-06-02'),
    (1303,3,'실적 서프라이즈','EARNINGS','2024-06-06','2024-06-09')
    on conflict do nothing;

insert into content.event_article(id, event_id, stock_id, article_id) values
    (1,1101,1,7),
    (2,1102,1,8),
    (3,1103,1,9),
    (4,1201,2,10),
    (5,1202,2,11),
    (6,1301,3,12),
    (7,1302,3,13),
    (8,1303,3,14)
    on conflict do nothing;

insert into content.stock_forecast(id, stock_id, horizon, direction, confidence, model) values
    (1,1,'SHORT','NEUTRAL',58,'Heuristic v0.2'),
    (2,1,'MID','DOWN',52,'Heuristic v0.2'),
    (3,1,'LONG','NEUTRAL',55,'Heuristic v0.2'),
    (4,2,'SHORT','UP',64,'Heuristic v0.2'),
    (5,2,'MID','UP',66,'Heuristic v0.2'),
    (6,2,'LONG','NEUTRAL',57,'Heuristic v0.2'),
    (7,3,'SHORT','UP',70,'Heuristic v0.2'),
    (8,3,'MID','UP',68,'Heuristic v0.2'),
    (9,3,'LONG','NEUTRAL',60,'Heuristic v0.2')
    on conflict do nothing;

insert into content.stock_forecast_bullet(id, forecast_id, position, text) values
    (1,1,1,'최근 수요 둔화 신호 확인'),
    (2,1,2,'가격 정책 변화 관찰'),
    (3,1,3,'단기 이벤트 일정 주의'),
    (4,2,1,'가이던스 하향 가능성 점검'),
    (5,2,2,'공급망 비용 압력 주의'),
    (6,2,3,'경쟁사 프로모션 영향 확인'),
    (7,3,1,'제품 믹스 개선 여부 관찰'),
    (8,3,2,'서비스 매출 기여도 점검'),
    (9,3,3,'정책/규제 리스크 모니터링'),
    (10,4,1,'AI 수요 확대가 긍정'),
    (11,4,2,'클라우드 성장 유지 여부 확인'),
    (12,4,3,'신규 고객 유입 흐름 체크'),
    (13,5,1,'가이던스 상향 여지'),
    (14,5,2,'기업 IT 지출 회복 주목'),
    (15,5,3,'파트너십 효과 확인'),
    (16,6,1,'경쟁 심화에 따른 마진 점검'),
    (17,6,2,'성장률 둔화 가능성 모니터링'),
    (18,6,3,'장기 투자 집행 계획 확인'),
    (19,7,1,'데이터센터 수요 강세'),
    (20,7,2,'반도체 사이클 상향'),
    (21,7,3,'공급망 병목 가능성 점검'),
    (22,8,1,'AI 인프라 투자 지속'),
    (23,8,2,'수주 가시성 개선'),
    (24,8,3,'가격 협상력 유지 확인'),
    (25,9,1,'장기 수요 전망은 견조'),
    (26,9,2,'성장 둔화 리스크 동시 존재'),
    (27,9,3,'신규 경쟁자 동향 주시')
    on conflict do nothing;

insert into content.stock_forecast_keyword(id, stock_id, keyword_id, position) values
    (1,1,3,1),
    (2,1,1,2),
    (3,1,5,3),
    (4,2,6,1),
    (5,2,7,2),
    (6,2,2,3),
    (7,3,8,1),
    (8,3,9,2),
    (9,3,1,3)
    on conflict do nothing;

select setval(pg_get_serial_sequence('content.keyword','id'), (select max(id) from content.keyword));
select setval(pg_get_serial_sequence('content.article','id'), (select max(id) from content.article));
select setval(pg_get_serial_sequence('content.stock_keyword','id'), (select max(id) from content.stock_keyword));
select setval(pg_get_serial_sequence('content.stock_article','id'), (select max(id) from content.stock_article));
select setval(pg_get_serial_sequence('content.price_event','id'), (select max(id) from content.price_event));
select setval(pg_get_serial_sequence('content.event_article','id'), (select max(id) from content.event_article));
select setval(pg_get_serial_sequence('content.stock_forecast','id'), (select max(id) from content.stock_forecast));
select setval(pg_get_serial_sequence('content.stock_forecast_bullet','id'), (select max(id) from content.stock_forecast_bullet));
select setval(pg_get_serial_sequence('content.stock_forecast_keyword','id'), (select max(id) from content.stock_forecast_keyword));
