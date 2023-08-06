# 지하철 노선도 미션
[ATDD 강의](https://edu.nextstep.camp/c/R89PYi5H) 실습을 위한 지하철 노선도 애플리케이션

## 목표
- 단위 테스트 vs 통합 테스트
- 외부 라이브러리 의존 객체를 TDD로 어떻게 구현할까?
<br>

# 학습 기록
## 💛2주차: 단위 테스트란
#### **기간**
- ```2023-07-06 - 2023-07-19```

#### Bullet point
- 테스트란?
- <Ouside->In>
- <Inside->Out>
- TDD는 무조건 좋은가?
- 결론
 TDD는 문제를 풀기 전에 문제를 명확히 정의(요구 사항 명세)하는 것에 주 목적을 둔 개발론입니다. 팀의 입장에서 TDD를 통해 개발한다면, 요구 사항 명세를 하고 시작하기에 문서화를 따로 할 필요가 없어서 동료 개발자 혹은 기획자(인수 테스트의 경우)와 협업하기에 용이합니다. 개인의 입장에서 TDD를 통해 개발한다면, 문제 정의를 명확히하여 방향성을 잡고 설계를 시작할 수 있어서 전체적인 개발 리소스를 효율적으로 다룰 수 있습니다.
 테스트는 상위 레이어에서 하위 레이어 방향으로 만 진행할 필요는 없습니다. 경험적으로 비추어 봤을 때 요구 사항 명세를 위해 최상위 레이어(인수 테스트) 작성을 먼저 한 후, 도메인 레이어를 상세하게 설계하며 TDD를 진행하는 것을 권장합니다.

#### 진행 미션
|Mission|Repository|Pull Request|
|------|---|---|
|실습 - 단위 테스트 작성|[michaelkimm/atdd-subway-path](https://github.com/michaelkimm/atdd-subway-path)|[pull request](https://github.com/next-step/atdd-subway-path/pull/600)|
|지하철 구간 추가 기능 개선|[michaelkimm/atdd-subway-path](https://github.com/michaelkimm/atdd-subway-path)|[pull request](https://github.com/next-step/atdd-subway-path/pull/610)|
|지하철 구간 제거 기능 개선|[michaelkimm/atdd-subway-path](https://github.com/michaelkimm/atdd-subway-path)|[pull request](https://github.com/next-step/atdd-subway-path/pull/610)|
|경로 조회 기능 기능|[michaelkimm/atdd-subway-path](https://github.com/michaelkimm/atdd-subway-path)|[pull request](https://github.com/next-step/atdd-subway-path/pull/623#issuecomment-1646744866)|


#### 블로그 포스팅
- [TDD 관점에서 테스트란? 장단점은?](https://ujkim-game.tistory.com/100)
