import { useMutation } from '@tanstack/react-query';

import { authAPI } from 'api';
import { QUERY_KEY } from 'constant';

import { UseCustomMutationOptions } from 'service/types';

import queryClient from 'api/config/queryClient';

function useDeleteRefreshToken(mutationOptions?: UseCustomMutationOptions<null>) {
  return useMutation(authAPI.deleteRefreshToken, {
    onSettled: () => {
      queryClient.refetchQueries([QUERY_KEY.DATA.AUTH]);
    },
    ...mutationOptions,
  });
}

export { useDeleteRefreshToken };
