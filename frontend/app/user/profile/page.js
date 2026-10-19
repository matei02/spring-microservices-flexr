'use client';

import { useState, useEffect } from 'react';
import Image from 'next/image';
import { FaUserEdit, FaCog, FaMedal } from 'react-icons/fa';
import LoadingState from '../../components/common/LoadingState';
import EmptyState from '../../components/common/EmptyState';
import { useAuth } from '../../hooks/useAuth';
import flexService from '../../services/flexService';

export default function ProfilePage() {
  const [isLoading, setIsLoading] = useState(true);
  const [userPosts, setUserPosts] = useState([]);
  const { user, isLoading: userLoading, getToken } = useAuth();

  useEffect(() => {
    const fetchUserPosts = async () => {
      if (!user) return;
      try {
        setIsLoading(true);
        const token = await getToken();
        const posts = await flexService.getPosts(token);
        // Filter posts by current user
        const filtered = posts.filter(post => post.authorId === user.id);
        setUserPosts(filtered);
      } catch (error) {
        console.error('Error fetching user posts:', error);
      } finally {
        setIsLoading(false);
      }
    };
    fetchUserPosts();
  }, [user]); // FIX: removed getToken from dependencies to prevent infinite re-render

  if (userLoading || isLoading) {
    return <LoadingState count={3} />;
  }

  if (!user) {
    return <EmptyState title="Not signed in" message="Please sign in to view your profile." />;
  }

  return (
    <div className="max-w-4xl mx-auto">
      {/* Profile Header */}
      <div className="card p-6 mb-6">
        <div className="flex flex-col md:flex-row md:items-start gap-8">
          <div className="relative w-32 h-32 mx-auto md:mx-0 avatar-outline">
            <Image
              src={user.picture || '/images/placeholder-avatar.svg'}
              alt={user.name || 'User Avatar'}
              fill
              className="rounded-full object-cover"
              sizes="(max-width: 768px) 128px, 128px"
              priority
            />
          </div>
          <div className="flex-grow text-center md:text-left">
            <div className="flex flex-col md:flex-row md:items-center md:justify-between mb-4">
              <div>
                <h1 className="text-2xl font-bold mb-1 text-white">{user.name}</h1>
                <p className="text-gray-400 mb-4">@{user.nickname || user.handle || user.email?.split('@')[0]}</p>
              </div>
              <div className="flex gap-2 justify-center md:justify-start">
                <button className="btn-primary flex items-center gap-2 px-4 py-2">
                  <FaUserEdit />
                  <span>Edit Profile</span>
                </button>
                <button className="btn-secondary p-2">
                  <FaCog className="w-5 h-5" />
                </button>
              </div>
            </div>
            <div className="flex justify-center md:justify-start gap-6 mb-4">
              <div className="text-center">
                <div className="font-bold text-xl text-white">{userPosts.length}</div>
                <div className="text-gray-400 text-sm">Posts</div>
              </div>
              {/* Followers/Following can be added if available in user object */}
            </div>
            <p className="text-white">{user.bio || ''}</p>
          </div>
        </div>
      </div>

      {/* Posts Grid */}
      {userPosts.length > 0 ? (
        <div className="grid grid-cols-3 gap-4">
          {userPosts.map((post) => (
            <div
              key={post.id}
              className="aspect-square relative card group cursor-pointer"
            >
              {post.image && (
                <Image
                  src={post.image}
                  alt={`Post by ${user.name}`}
                  fill
                  sizes="(max-width: 768px) 33vw, (max-width: 1200px) 25vw, 20vw"
                  className="object-cover transition-transform duration-300 group-hover:scale-105"
                />
              )}
              <div className="absolute inset-0 bg-black bg-opacity-0 group-hover:bg-opacity-20 transition-all duration-300" />
            </div>
          ))}
        </div>
      ) : (
        <div className="card flex flex-col items-center justify-center py-16">
          <EmptyState
            title="No achievements yet"
            message="Share your first fitness achievement!"
            icon={FaMedal}
          />
        </div>
      )}
    </div>
  );
}
