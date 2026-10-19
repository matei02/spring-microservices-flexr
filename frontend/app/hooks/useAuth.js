'use client';

import { useAuth0 } from '@auth0/auth0-react';
import { useEffect, useState } from 'react';
import authService from '../services/authService';
import { useUser } from '../context/UserContext';

export const useAuth = () => {
  const {
    isAuthenticated,
    isLoading,
    user: auth0User,
    loginWithRedirect,
    logout,
    getAccessTokenSilently,
    error: auth0Error
  } = useAuth0();

  const [roles, setRoles] = useState([]);
  const [isAdmin, setIsAdmin] = useState(false);
  const [error, setError] = useState(null);
  const { user, setUser } = useUser();
  const [onboardingRequired, setOnboardingRequired] = useState(false);

  useEffect(() => {
    if (auth0Error) {
      console.error('Auth0 hook error:', auth0Error);
      setError(auth0Error.message);
    }
  }, [auth0Error]);

  const getToken = async () => {
    try {
      if (!isAuthenticated) {
        console.warn('Tried to get token but user is not authenticated');
        return null;
      }
      const token = await getAccessTokenSilently({
        audience: process.env.NEXT_PUBLIC_AUTH0_AUDIENCE,
        scope: 'openid profile email'
      });
      if (!token) {
        console.error('getAccessTokenSilently returned null or empty token');
      }
      return token;
    } catch (err) {
      setError('Token error: ' + err.message);
      console.error('Error getting token:', err);
      return null;
    }
  };

  useEffect(() => {
    const fetchOrOnboardUser = async () => {
      if (isAuthenticated && auth0User && !user) {
        try {
          const token = await getToken();
          const flexrUser = await authService.getUserByAuth0Id(auth0User.sub, token);
          setUser(flexrUser);
          setOnboardingRequired(false);
        } catch (err) {
          // User not found, require onboarding
          setOnboardingRequired(true);
        }
      }
    };
    fetchOrOnboardUser();
  }, [isAuthenticated, auth0User, user, setUser]);

  useEffect(() => {
    if (user) {
      const userRoles = user.roles || auth0User?.['https://your-tenant.eu.auth0.com/roles'] || [];
      setRoles(userRoles);
      setIsAdmin(userRoles.includes('admin'));
    }
  }, [user, auth0User]);

  const login = async () => {
    try {
      await loginWithRedirect({
        appState: { returnTo: window.location.pathname },
        audience: 'https://app.flexr.online/auth0/audience/api', // Replace with your Auth0 API identifier
        scope: 'openid profile email' // Add any additional scopes your app/backend needs
      });
    } catch (err) {
      setError(err.message);
    }
  };

  return {
    isAuthenticated,
    isLoading,
    user,
    auth0User,
    login,
    logout: () => logout({ returnTo: window.location.origin }),
    getToken,
    roles,
    isAdmin,
    error,
    onboardingRequired,
    setUser
  };
};
