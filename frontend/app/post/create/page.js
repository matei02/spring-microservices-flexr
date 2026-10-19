'use client';

import { useState, useRef } from 'react';
import { useRouter } from 'next/navigation';
import Image from 'next/image';
import { FaImage, FaVideo } from 'react-icons/fa';
import flexService from '../../services/flexService';
import { useAuth } from '../../hooks/useAuth';

export default function CreatePost() {
  const router = useRouter();
  const [content, setContent] = useState('');
  const [imageFile, setImageFile] = useState(null);
  const [imagePreview, setImagePreview] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const fileInputRef = useRef(null);
  const { user, getToken } = useAuth();

  const handleImageClick = () => {
    fileInputRef.current?.click();
  };

  const handleImageChange = (e) => {
    const file = e.target.files?.[0];
    if (file) {
      setImageFile(file);
      const preview = URL.createObjectURL(file);
      setImagePreview(preview);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!user) {
      alert('You must be signed in to post.');
      return;
    }
    if (!content && !imageFile) {
      alert('Please add some content or an image');
      return;
    }
    try {
      setIsLoading(true);
      const token = await getToken();
      await flexService.createPost({
        content,
        image: imageFile,
        authorId: user.id,
      }, token);
      router.push('/');
      router.refresh();
    } catch (error) {
      console.error('Error creating post:', error);
      alert('Failed to create post. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="max-w-2xl mx-auto">
      <h1 className="text-2xl font-bold mb-6 text-white">Create New Flex</h1>
      
      <form onSubmit={handleSubmit} className="space-y-6">
        {/* Text Content */}
        <div>
          <label htmlFor="content" className="block text-sm font-medium text-gray-200 mb-2">
            What&apos;s your achievement?
          </label>
          <textarea
            id="content"
            value={content}
            onChange={(e) => setContent(e.target.value)}
            rows={4}
            className="w-full rounded-lg bg-zinc-900 border-zinc-800 text-white placeholder-gray-400 focus:border-blue-500 focus:ring-blue-500"
            placeholder="Share your achievement..."
          />
        </div>

        {/* Media Upload */}
        <div className="space-y-4">
          <label className="block text-sm font-medium text-gray-200">
            Add Media
          </label>
          
          <div className="grid grid-cols-2 gap-4">
            <button
              type="button"
              onClick={handleImageClick}
              className="flex items-center justify-center gap-2 p-4 border-2 border-dashed border-zinc-800 rounded-lg hover:border-zinc-700 transition-colors bg-zinc-900"
            >
              <FaImage className="w-6 h-6 text-white" />
              <span className="text-white">Add Image</span>
            </button>
            
            <input
              ref={fileInputRef}
              type="file"
              accept="image/*"
              onChange={handleImageChange}
              className="hidden"
            />
          </div>
        </div>

        {/* Preview */}
        {imagePreview && (
          <div>
            <h3 className="text-lg font-medium mb-2 text-white">Preview</h3>
            <div className="relative aspect-video bg-zinc-900 rounded-lg overflow-hidden">
              <Image
                src={imagePreview}
                alt="Preview"
                fill
                className="object-contain"
              />
            </div>
          </div>
        )}

        {/* Submit Button */}
        <div>
          <button
            type="submit"
            disabled={isLoading || (!content && !imageFile)}
            className="w-full bg-blue-600 text-white py-2 px-4 rounded-lg hover:bg-blue-700 transition-colors disabled:bg-zinc-800 disabled:text-gray-400 disabled:cursor-not-allowed"
          >
            {isLoading ? 'Sharing...' : 'Share Flex'}
          </button>
        </div>
      </form>
    </div>
  );
}
