import { FormEventHandler, useContext, useEffect, useRef, useState } from 'react';
import { useMutation, useQueryClient } from 'react-query';
import API from 'src/apis/requests';
import { ERROR_MESSAGE, REVIEW } from 'src/constants';
import { modalContext } from '../Modal/ModalProvider';
import { Form, Content, EditButton, DeleteButton } from './ReviewEditForm.styles';
interface Props {
  drinkId: string;
  review: Review.ReviewItem;
}

const ReviewEditForm = ({ drinkId, review }: Props) => {
  const { id: reviewId, content, createdAt, modifiedAt } = review;

  const textAreaRef = useRef<HTMLTextAreaElement>(null);

  const closeModal = useContext(modalContext)?.closeModal;

  const [editContent, setEditContent] = useState(content);

  const queryClient = useQueryClient();

  const { mutate: deleteReview } = useMutation(
    () => API.deleteReview<string>(drinkId, reviewId.toString()),
    {
      onSuccess: () => {
        queryClient.invalidateQueries('reviews');
        closeModal?.();
      },
      onError: () => {
        alert(ERROR_MESSAGE.DEFAULT);
      },
    }
  );

  const { mutate: editReview } = useMutation(
    () =>
      API.editReview<string, Review.ReviewRequestData>(drinkId, reviewId.toString(), {
        content: editContent,
      }),
    {
      onSuccess: () => {
        queryClient.invalidateQueries('reviews');
        closeModal?.();
      },
      onError: () => {
        alert(ERROR_MESSAGE.DEFAULT);
      },
    }
  );

  useEffect(() => {
    if (textAreaRef.current) {
      textAreaRef.current.focus();
      textAreaRef.current.setSelectionRange(review.content.length, review.content.length);
    }

    setEditContent(review.content);
  }, [review]);

  const onEdit: FormEventHandler<HTMLFormElement> = (event) => {
    event.preventDefault();

    editReview();
  };

  const onDelete = () => {
    if (confirm('리뷰를 삭제하시겠습니까?')) {
      deleteReview();
    }
  };

  return (
    <>
      <Form onSubmit={onEdit}>
        <h2>리뷰 수정하기</h2>
        <Content>
          <div>
            <span>{new Date(createdAt).toLocaleDateString()}</span>
            <span>{`${editContent.length}/${REVIEW.MAX_LENGTH}`}</span>
          </div>
          <textarea
            value={editContent}
            onChange={({ target }) => setEditContent(target.value)}
            ref={textAreaRef}
            placeholder="리뷰를 작성해 주세요"
            maxLength={REVIEW.MAX_LENGTH}
            required
          />
        </Content>

        <EditButton disabled={!editContent}>수정하기</EditButton>
      </Form>
      <DeleteButton type="button" onClick={onDelete}>
        삭제하기
      </DeleteButton>
    </>
  );
};

export default ReviewEditForm;
