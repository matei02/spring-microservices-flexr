'use client';

import { useAuth } from '../../hooks/useAuth';
import LoginPromptModal from './LoginPromptModal';
import OnboardingForm from './OnboardingForm';

export default function ProtectedRoute({ children, adminOnly = false }) {
  const { isAuthenticated, isLoading, isAdmin, onboardingRequired, auth0User, setUser } = useAuth();

  // Show loading state while checking authentication
  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="animate-spin rounded-full h-8 w-8 border-t-2 border-white"></div>
      </div>
    );
  }

  // If admin route and user is not admin, show access denied
  if (adminOnly && !isAdmin) {
    return (
      <div className="flex flex-col items-center justify-center min-h-screen text-white">
        <h1 className="text-2xl font-bold mb-4">Access Denied</h1>
        <p>You do not have permission to access this page.</p>
      </div>
    );
  }

  // If not authenticated, show blurred content and login modal
  if (!isAuthenticated) {
    return (
      <>
        <div className="filter blur-sm pointer-events-none">
          {children}
        </div>
        <LoginPromptModal />
      </>
    );
  }

  // Show onboarding form if required, otherwise show content
  if (onboardingRequired && auth0User) {
    return <OnboardingForm auth0User={auth0User} onComplete={() => setUser({ ...auth0User })} />;
  }

  // If authenticated and has proper permissions, show content
  return <>{children}</>;
}
