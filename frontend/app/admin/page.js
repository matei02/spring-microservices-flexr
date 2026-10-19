'use client';

import { useAuth } from '../hooks/useAuth';

export default function AdminDashboard() {
  const { user } = useAuth();

  return (
    <div className="max-w-4xl mx-auto">
      <h1 className="text-2xl font-bold mb-6 text-white">Admin Dashboard</h1>
      <div className="bg-black border border-zinc-900 rounded-xl p-6">
        <h2 className="text-xl font-semibold text-white mb-4">Welcome, {user?.name}</h2>
        <div className="space-y-4">
          {/* Add admin features here */}
          <div className="p-4 bg-zinc-900 rounded-lg">
            <h3 className="text-white font-medium mb-2">User Management</h3>
            <p className="text-gray-400">Manage users and their roles</p>
          </div>
          <div className="p-4 bg-zinc-900 rounded-lg">
            <h3 className="text-white font-medium mb-2">Content Moderation</h3>
            <p className="text-gray-400">Review and moderate user posts</p>
          </div>
        </div>
      </div>
    </div>
  );
}
