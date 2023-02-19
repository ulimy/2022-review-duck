import { ReactNode, Suspense } from 'react';
import { BrowserRouter } from 'react-router-dom';

import { QueryClientProvider } from '@tanstack/react-query';

import queryClient from 'api/config/queryClient';
import UserAgentProvider from 'common/contexts/UserAgent';
import ModalProvider from 'components/ModalProvider';
import { RecoilRoot } from 'recoil';
import 'styles/@global.scss';

import PageRoutes from 'routes';

import { ErrorBoundary, Snackbar, PageSuspense } from 'common/components';

import ErrorPage from 'pages/ErrorPage';

function ContextWrapper({ children }: { children: ReactNode }) {
  return (
    <RecoilRoot>
      <BrowserRouter>
        <QueryClientProvider client={queryClient}>
          <ModalProvider />
          <UserAgentProvider>
            <PageSuspense.Provider>{children}</PageSuspense.Provider>
          </UserAgentProvider>
        </QueryClientProvider>
      </BrowserRouter>
    </RecoilRoot>
  );
}

function App() {
  return (
    <ContextWrapper>
      <Snackbar.Provider />
      <Suspense>
        <ErrorBoundary fallback={ErrorPage}>
          <PageRoutes />
        </ErrorBoundary>
      </Suspense>
    </ContextWrapper>
  );
}

export default App;
