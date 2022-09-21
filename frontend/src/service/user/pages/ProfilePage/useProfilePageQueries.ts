import { TAB } from 'constant';
import { Tabs } from 'types';

import { useDeleteReviewAnswer, useDeleteReviewForm } from 'service/@shared/hooks/queries/review';
import { useDeleteTemplate } from 'service/@shared/hooks/queries/template/useDelete';
import {
  useGetUserReviewForms,
  useGetUserReviewAnswer,
  useGetUserProfile,
  useGetUserTemplates,
} from 'service/@shared/hooks/queries/user';

function useProfilePageQueries(currentTab: Tabs, socialIdPrams: string, pageNumber: string) {
  const socialId = Number(socialIdPrams);

  const useGetQueries = {
    [TAB.USER_PROFILE.REVIEWS]: useGetUserReviewAnswer,
    [TAB.USER_PROFILE.REVIEW_FORMS]: useGetUserReviewForms,
    [TAB.USER_PROFILE.TEMPLATES]: useGetUserTemplates,
  };

  const deleteReviewMutation = useDeleteReviewAnswer();
  const deleteReviewFormMutation = useDeleteReviewForm();
  const deleteTemplateMutation = useDeleteTemplate();

  const getUserProfile = useGetUserProfile({ socialId });
  const getUserArticles = useGetQueries[currentTab](socialId, pageNumber);

  const isLoading = getUserProfile.isLoading || getUserArticles.isLoading;
  const isError = getUserProfile.isError || getUserArticles.isError;

  if (isLoading || isError) return false;

  return {
    userArticles: getUserArticles.data,
    userProfile: getUserProfile.data,
    deleteReviewMutation,
    deleteReviewFormMutation,
    deleteTemplateMutation,
  };
}

export default useProfilePageQueries;
