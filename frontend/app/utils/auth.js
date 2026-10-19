export const auth0Config = {
  domain: process.env.NEXT_PUBLIC_AUTH0_DOMAIN,
  clientId: process.env.NEXT_PUBLIC_AUTH0_CLIENT_ID,
  authorizationParams: {
    redirect_uri: typeof window !== 'undefined' ? window.location.origin : '',
  },
};

export const AUTH_CONFIG = {
  domain: process.env.NEXT_PUBLIC_AUTH0_DOMAIN,
  clientId: process.env.NEXT_PUBLIC_AUTH0_CLIENT_ID,
  audience: process.env.NEXT_PUBLIC_AUTH0_AUDIENCE,
};

export const ROLES = {
  ADMIN: 'admin',
  USER: 'user',
};

export const hasRole = (user, role) => {
  if (!user || !user['https://your-tenant.eu.auth0.com/roles']) {
    return false;
  }
  return user['https://your-tenant.eu.auth0.com/roles'].includes(role);
};

export const isAdmin = (user) => hasRole(user, ROLES.ADMIN);

export const getProtectedRoute = (session, loading) => {
  // Add your protected route logic here
  const isAuthenticated = !!session;
  const isLoading = loading;
  
  return { isAuthenticated, isLoading };
};

export const getUserProfile = (user) => {
  if (!user) return null;
  
  return {
    id: user.sub,
    email: user.email,
    name: user.name,
    picture: user.picture,
    nickname: user.nickname,
  };
};
