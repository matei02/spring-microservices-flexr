'use client';

import { Auth0Provider } from '@auth0/auth0-react';
import { useCallback } from 'react';
import { useRouter } from 'next/navigation';

export default function Auth0ProviderWrapper({ children }) {
  const router = useRouter();
  
  // These are exposed to the client-side, so it's safe to use NEXT_PUBLIC_ prefix
  const domain = process.env.NEXT_PUBLIC_AUTH0_DOMAIN;
  const clientId = process.env.NEXT_PUBLIC_AUTH0_CLIENT_ID;

  const onRedirectCallback = useCallback((appState) => {
    router.push(appState?.returnTo || '/');
  }, [router]);

  if (!(domain && clientId)) {
    console.error('Missing required Auth0 configuration:', { 
      domain: !!domain, 
      clientId: !!clientId 
    });
    return null;
  }

  return (
    <Auth0Provider
      domain={domain}
      clientId={clientId}
      authorizationParams={{
        redirect_uri: typeof window !== 'undefined' ? window.location.origin : '',
        audience: 'https://app.flexr.online/auth0/audience/api',
        scope: 'openid profile email'
      }}
      useRefreshTokens={true}
      useRefreshTokensFallback={true}
      cacheLocation="localstorage"
      onRedirectCallback={onRedirectCallback}
    >
      {children}
    </Auth0Provider>
  );
}
