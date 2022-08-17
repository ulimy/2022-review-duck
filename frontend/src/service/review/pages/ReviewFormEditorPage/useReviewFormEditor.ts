import { UseMutationResult } from 'react-query';

import {
  ReviewForm,
  CreateReviewFormRequest,
  UpdateReviewFormRequest,
  ErrorResponse,
} from 'service/@shared/types';

import {
  useCreateReviewForm,
  useGetReviewForm,
  useUpdateReviewForm,
} from 'service/@shared/hooks/queries/review';

type SubmitMutationResult = UseMutationResult<
  { reviewFormCode: string },
  ErrorResponse,
  CreateReviewFormRequest | UpdateReviewFormRequest
>;

function useReviewFormEditor(reviewFormCode: string) {
  const createMutation = useCreateReviewForm();
  const updateMutation = useUpdateReviewForm();

  const submitMutation = reviewFormCode ? updateMutation : createMutation;

  const getReviewFormQuery = useGetReviewForm(reviewFormCode, {
    enabled: !!reviewFormCode,
  });

  const initialReviewForm: ReviewForm = getReviewFormQuery.data || {
    title: '',
    questions: [
      {
        value: '',
      },
    ],
    info: {
      creator: {
        profileUrl: '',
        nickname: '알 수 없음',
      },
      isSelf: false,
      updateDate: '오류',
    },
  };

  return {
    initialReviewForm,
    isNewReviewForm: !reviewFormCode,
    isLoadError: getReviewFormQuery.isError,
    loadError: getReviewFormQuery.error,
    isSubmitLoading: submitMutation.isLoading,
    submitReviewForm: submitMutation as SubmitMutationResult,
  };
}

export default useReviewFormEditor;