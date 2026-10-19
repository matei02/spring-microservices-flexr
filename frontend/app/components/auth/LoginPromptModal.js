'use client';

import { useAuth } from '../../hooks/useAuth';

export default function LoginPromptModal() {
  const { login, error } = useAuth();

  const handleLogin = async () => {
    try {
      await login();
    } catch (err) {
      console.error('Login error:', err);
    }
  };

  return (
    <div className="fixed inset-0 z-[100] bg-black/50 flex items-center justify-center">
      <div className="bg-zinc-900 p-8 rounded-lg shadow-xl max-w-md w-full mx-4 backdrop-blur-none transform scale-100">
        <h2 className="text-2xl font-bold text-white mb-4">Welcome to FlexR</h2>
        <p className="text-zinc-400 mb-6">
          Join our community to share your achievements and connect with others.
        </p>
        {error && (
          <div className="bg-red-900/50 border border-red-500 text-red-200 px-4 py-2 rounded-md mb-4">
            {error}
          </div>
        )}
        <button
          onClick={handleLogin}
          className="w-full bg-white text-black py-3 px-6 rounded-lg font-semibold 
                   hover:bg-zinc-200 transition-colors duration-200 flex items-center justify-center"
        >
          Sign In / Sign Up
        </button>
        <p className="text-zinc-500 text-sm mt-4 text-center">
          By continuing, you agree to our Terms of Service and Privacy Policy.
        </p>
      </div>
    </div>
  );
}
