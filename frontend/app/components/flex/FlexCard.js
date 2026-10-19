'use client';

import { useState, useEffect } from 'react';
import Image from 'next/image';
import Link from 'next/link';
import { FaHeart, FaComment, FaShare, FaBookmark } from 'react-icons/fa';
import flexService from '../../services/flexService';
import { useAuth } from '../../hooks/useAuth';

export default function FlexCard({ flex }) {
  const { getToken, user } = useAuth();
  const [likes, setLikes] = useState(0);
  const [isLiked, setIsLiked] = useState(false);
  const [likeId, setLikeId] = useState(null);
  const [isBookmarked, setIsBookmarked] = useState(false);
  const [likeLoading, setLikeLoading] = useState(false);
  const [authorInfo, setAuthorInfo] = useState(null); // New: state for fallback user info

  useEffect(() => {
    // Fetch author info if flex.user is missing but authorId is present
    const fetchAuthorInfo = async () => {
      if (!flex.user && flex.authorId) {
        try {
          const token = await getToken();
          // Assuming flexService.getUserById exists; otherwise, skip
          if (flexService.getUserById) {
            const info = await flexService.getUserById(flex.authorId, token);
            setAuthorInfo(info);
          }
        } catch (e) {
          setAuthorInfo(null);
        }
      }
    };
    fetchAuthorInfo();
  }, [flex.user, flex.authorId, getToken]);

  useEffect(() => {
    const fetchLikes = async () => {
      if (!user) return;
      try {
        const token = await getToken();
        const allLikes = await flexService.getLikes(token);
        const postLikes = allLikes.filter(like => like.postId === flex.id);
        setLikes(postLikes.length);
        const userLike = postLikes.find(like => String(like.userId) === String(user.id));
        if (userLike) {
          setIsLiked(true);
          setLikeId(userLike.id);
        } else {
          setIsLiked(false);
          setLikeId(null);
        }
      } catch (error) {
        console.error('Error fetching likes:', error);
      }
    };
    fetchLikes();
  }, [flex.id, user, getToken]);

  // Early return after hooks declaration
  if (!flex) return null;

  const handleLike = async () => {
    if (!user || likeLoading) return;
    setLikeLoading(true);
    try {
      const token = await getToken();
      // Always fetch latest likes for this post before acting
      const allLikes = await flexService.getLikes(token);
      const postLikes = allLikes.filter(like => like.postId === flex.id);
      const userLike = postLikes.find(like => String(like.userId) === String(user.id));
      if (!userLike) {
        // Not liked yet, so like it
        const res = await flexService.createLike({ postId: flex.id, userId: user.id }, token);
        setIsLiked(true);
        setLikeId(res.id);
        setLikes(postLikes.length + 1);
      } else {
        // Already liked, so unlike it
        await flexService.deleteLike(userLike.id, token);
        setIsLiked(false);
        setLikeId(null);
        setLikes(postLikes.length - 1 >= 0 ? postLikes.length - 1 : 0);
      }
    } catch (error) {
      console.error('Error liking/unliking post:', error);
    } finally {
      setLikeLoading(false);
    }
  };

  const handleBookmark = () => {
    setIsBookmarked(!isBookmarked);
  };

  const handleShare = async () => {
    try {
      await navigator.share({
        title: `Flex Post`,
        text: flex.content,
        url: window.location.href,
      });
    } catch (error) {
      console.error('Error sharing:', error);
    }
  };

  // Helper to get display handle/name/email
  const getDisplayHandle = () => {
    if (flex.user?.handle) return flex.user.handle;
    if (authorInfo?.handle) return authorInfo.handle;
    if (flex.user?.fullName) return flex.user.fullName;
    if (authorInfo?.fullName) return authorInfo.fullName;
    if (flex.user?.email) return flex.user.email;
    if (authorInfo?.email) return authorInfo.email;
    if (flex.authorId) return '@' + flex.authorId;
    return 'Unknown User';
  };
  const getDisplayName = () => {
    if (flex.user?.fullName) return flex.user.fullName;
    if (authorInfo?.fullName) return authorInfo.fullName;
    if (flex.user?.email) return flex.user.email;
    if (authorInfo?.email) return authorInfo.email;
    return '';
  };

  return (
    <article className="card mb-6 overflow-hidden">
      {/* User header */}
      <div className="flex items-center p-4">
        {/* Avatar removed */}
        <div className="ml-3">
          <Link href={flex.user?.handle ? `/user/${flex.user.handle.replace('@', '')}` : '#'} className="font-semibold text-white hover:text-gray-300">
            {getDisplayHandle()}
          </Link>
          <p className="text-sm text-gray-400">{getDisplayName()}</p>
        </div>
      </div>

      {/* Post image */}
      {flex.image && (
        <div className="relative aspect-square bg-zinc-900">
          <Image
            src={`data:image/*;base64,${flex.image}`}
            alt={flex.content ? flex.content.slice(0, 40) : 'Flex post image'}
            fill
            sizes="(max-width: 768px) 100vw, (max-width: 1200px) 50vw, 33vw"
            priority
            className="object-cover"
          />
        </div>
      )}

      {/* Actions */}
      <div className="p-4">
        <div className="flex justify-between mb-4">
          <div className="flex space-x-4">
            <button
              onClick={handleLike}
              disabled={likeLoading}
              className={`flex items-center space-x-1 transition-colors ${isLiked ? 'text-red-500' : 'text-white hover:text-red-500'
                }`}
            >
              <FaHeart className="w-6 h-6" />
              <span className="text-sm font-medium">{likes}</span>
            </button>
            <button className="flex items-center space-x-1 text-white hover:text-blue-400 transition-colors">
              <FaComment className="w-6 h-6" />
              <span className="text-sm font-medium">{flex.comments || 0}</span>
            </button>
            <button onClick={handleShare} className="text-white hover:text-green-400 transition-colors">
              <FaShare className="w-6 h-6" />
            </button>
          </div>
          <button
            onClick={handleBookmark}
            className={`transition-colors ${isBookmarked ? 'text-yellow-500' : 'text-white hover:text-yellow-500'
              }`}
          >
            <FaBookmark className="w-6 h-6" />
          </button>
        </div>

        {/* Content */}
        <div className="space-y-2">
          <p className="text-sm text-white">
            <Link href={flex.user?.handle ? `/user/${flex.user.handle.replace('@', '')}` : '#'} className="font-semibold hover:text-gray-300 mr-2">
              {getDisplayName() || 'Unknown User'}
            </Link>
            {flex.content}
          </p>
          <p className="text-gray-400 text-xs">{new Date(flex.createdAt).toLocaleDateString(undefined, { year: 'numeric', month: 'long', day: 'numeric' })}</p>
        </div>
      </div>
    </article>
  );
}
