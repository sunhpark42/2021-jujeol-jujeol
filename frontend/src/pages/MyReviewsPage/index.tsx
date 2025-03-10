import { useEffect, useRef } from 'react';
import { useInfiniteQuery } from 'react-query';
import { useHistory } from 'react-router-dom';
import API from 'src/apis/requests';
import Arrow from 'src/components/@shared/Arrow/Arrow';
import InfinityScrollPoll from 'src/components/@shared/InfinityScrollPoll/InfinityScrollPoll';
import PersonalReviewItem from 'src/components/Item/PersonalReviewItem';
import useInfinityScroll from 'src/hooks/useInfinityScroll';
import { Container, Header } from './styles';

const MyReviewsPage = () => {
  const history = useHistory();

  const {
    data: { pages } = {},
    fetchNextPage,
    hasNextPage,
  } = useInfiniteQuery(
    'personal-review',
    ({ pageParam = 1 }) => API.getPersonalReviews({ page: pageParam, size: 10 }),
    {
      retry: 0,
      getNextPageParam: ({ pageInfo }) => {
        const { currentPage, lastPage } = pageInfo;

        return currentPage < lastPage ? currentPage + 1 : undefined;
      },
    }
  );

  const observerTargetRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    window.scrollTo(0, 0);
  }, []);
  useInfinityScroll({ target: observerTargetRef, fetchNextPage, hasNextPage });

  const personalReviews = pages?.map((page) => page.data).flat();

  const onMoveGoBack = () => history.goBack();

  return (
    <>
      <Header>
        <button type="button" onClick={onMoveGoBack}>
          <Arrow size="0.7rem" borderWidth="2px" dir="LEFT" />
        </button>
        <h2>내가 남긴 리뷰</h2>
      </Header>

      <Container>
        {personalReviews?.map((review) => (
          <PersonalReviewItem key={review.id} review={review} />
        ))}
        <InfinityScrollPoll ref={observerTargetRef} />
      </Container>
    </>
  );
};

export default MyReviewsPage;
