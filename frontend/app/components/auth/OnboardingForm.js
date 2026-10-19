import { useState } from 'react';
import authService from '../../services/authService';
import { useUser } from '../../context/UserContext';
import { useAuth } from '../../hooks/useAuth';

export default function OnboardingForm({ auth0User, onComplete }) {
  const [fullName, setFullName] = useState('');
  const [handle, setHandle] = useState('');
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);
  const { setUser } = useUser();
  const { getToken, logout } = useAuth();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    if (!fullName.trim() || !handle.trim()) {
      setError('Both fields are required.');
      return;
    }
    setLoading(true);
    try {
      const token = await getToken();
      const userObj = {
        fullName,
        handle,
        auth0Id: auth0User.sub,
        email: auth0User.email,
      };
      await authService.createUser(userObj);
      // Fetch the user from backend to get the full user object
      const backendUser = await authService.getUserByAuth0Id(auth0User.sub, token);
      setUser(backendUser);
      setSuccess(true);
      onComplete && onComplete();
    } catch (err) {
      const errorMessage = err.response?.data?.message || 'Failed to create account.';
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  if (success) {
    return (
      <div className="fixed inset-0 z-[100] bg-black/70 flex items-center justify-center">
        <div className="bg-zinc-900 p-8 rounded-xl shadow-xl w-full max-w-md mx-4 text-center">
          <h2 className="text-2xl font-bold text-white mb-4">Account Created!</h2>
          <p className="text-zinc-300 mb-4">Welcome to FlexR. You can now start using the app.</p>
          <button
            className="btn-primary w-full py-3 font-semibold mt-4"
            onClick={() => window.location.reload()}
          >
            Refresh Page
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="fixed inset-0 z-[100] bg-black/70 flex items-center justify-center">
      <form onSubmit={handleSubmit} className="bg-zinc-900 p-8 rounded-xl shadow-xl w-full max-w-md mx-4">
        <h2 className="text-2xl font-bold text-white mb-4">Set Up Your FlexR Account</h2>
        <div className="mb-4">
          <label className="block text-zinc-300 mb-1">Full Name</label>
          <input
            type="text"
            className="w-full p-3 rounded-lg bg-zinc-800 text-white border border-zinc-700 focus:outline-none focus:ring-2 focus:ring-blue-600"
            value={fullName}
            onChange={e => setFullName(e.target.value)}
            required
          />
        </div>
        <div className="mb-4">
          <label className="block text-zinc-300 mb-1">FlexR Tag (handle)</label>
          <input
            type="text"
            className="w-full p-3 rounded-lg bg-zinc-800 text-white border border-zinc-700 focus:outline-none focus:ring-2 focus:ring-blue-600"
            value={handle}
            onChange={e => setHandle(e.target.value)}
            required
            placeholder="@yourhandle"
          />
        </div>
        {error && <div className="text-red-400 mb-4">{error}</div>}
        <button
          type="submit"
          className="w-full btn-primary py-3 font-semibold flex items-center justify-center mb-2"
          disabled={loading}
        >
          {loading ? 'Creating Account...' : 'Create Account'}
        </button>
        <button
          type="button"
          className="w-full py-2 mt-2 rounded bg-zinc-700 text-white hover:bg-zinc-600"
          onClick={logout}
        >
          Log out
        </button>
      </form>
    </div>
  );
}
