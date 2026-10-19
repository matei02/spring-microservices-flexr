'use client';

import Link from 'next/link';
import { useRef, useEffect, useState } from 'react';
import { 
  FaHome, 
  FaSearch, 
  FaUser, 
  FaPlusCircle, 
  FaInbox,
  FaSignOutAlt,
  FaCog
} from 'react-icons/fa';
import { useAuth } from '../../hooks/useAuth';

export default function Navigation() {
  const { isAuthenticated, user, isAdmin, login, logout } = useAuth();
  const [showUserMenu, setShowUserMenu] = useState(false);
  const menuRef = useRef(null);
  const buttonRef = useRef(null);

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (
        menuRef.current && 
        !menuRef.current.contains(event.target) &&
        !buttonRef.current.contains(event.target)
      ) {
        setShowUserMenu(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  return (
    <>
      {/* Top Navbar */}
      <nav className="fixed top-0 w-full navbar border-b z-50">
        <div className="max-w-5xl mx-auto px-4">
          <div className="flex justify-between items-center h-16">
            <Link href="/" className="text-2xl font-bold text-white hover:text-gray-300 transition-colors">
              FlexR
            </Link>
            
            {isAuthenticated ? (
              <div className="flex items-center space-x-1 md:space-x-4">
                <Link 
                  href="/trending" 
                  className="p-3 hover:bg-zinc-900 rounded-full transition-all"
                  title="Trending"
                >
                  <span className="text-2xl">🔥</span>
                </Link>
                <Link 
                  href="/messages" 
                  className="p-3 text-white hover:text-gray-300 hover:bg-zinc-900 rounded-full transition-all"
                  title="Messages"
                >
                  <FaInbox className="w-6 h-6" />
                </Link>
              </div>
            ) : (
              <button
                onClick={() => login()}
                className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition-colors"
              >
                Sign In
              </button>
            )}
          </div>
        </div>
      </nav>

      {/* Bottom Footer */}
      {isAuthenticated && (
        <nav className="fixed bottom-0 w-full footer border-t z-50">
          <div className="max-w-5xl mx-auto px-4">
            <div className="flex justify-around items-center h-16">
              <Link 
                href="/" 
                className="p-3 text-white hover:text-gray-300 hover:bg-zinc-900 rounded-full transition-all"
                title="Home"
              >
                <FaHome className="w-6 h-6" />
              </Link>
              <Link 
                href="/search" 
                className="p-3 text-white hover:text-gray-300 hover:bg-zinc-900 rounded-full transition-all"
                title="Search"
              >
                <FaSearch className="w-6 h-6" />
              </Link>
              <Link 
                href="/post/create" 
                className="p-3 text-white hover:text-gray-300 hover:bg-zinc-900 rounded-full transition-all"
                title="Create Post"
              >
                <FaPlusCircle className="w-6 h-6" />
              </Link>
              
              <div className="relative">
                <button
                  ref={buttonRef}
                  onClick={() => setShowUserMenu(!showUserMenu)}
                  className="p-3 text-white hover:text-gray-300 hover:bg-zinc-900 rounded-full transition-all"
                  title="Profile"
                >
                  <FaUser className="w-6 h-6" />
                </button>

                {showUserMenu && (
                  <div 
                    ref={menuRef}
                    className="absolute bottom-full right-0 mb-2 w-48 bg-black border border-zinc-800 rounded-lg shadow-lg overflow-hidden"
                  >
                    <div className="p-2 border-b border-zinc-800">
                      <p className="text-sm text-white font-semibold">{user?.name}</p>
                      <p className="text-xs text-gray-400">{user?.email}</p>
                    </div>
                    <div className="p-1">
                      <Link
                        href="/user/profile"
                        className="flex items-center gap-2 px-3 py-2 text-white hover:bg-zinc-900 rounded-md"
                        onClick={() => setShowUserMenu(false)}
                      >
                        <FaUser className="w-4 h-4" />
                        <span>Profile</span>
                      </Link>
                      {isAdmin && (
                        <Link
                          href="/admin"
                          className="flex items-center gap-2 px-3 py-2 text-white hover:bg-zinc-900 rounded-md"
                          onClick={() => setShowUserMenu(false)}
                        >
                          <FaCog className="w-4 h-4" />
                          <span>Admin Dashboard</span>
                        </Link>
                      )}
                      <button
                        onClick={() => {
                          setShowUserMenu(false);
                          logout();
                        }}
                        className="flex items-center gap-2 px-3 py-2 text-red-500 hover:bg-zinc-900 rounded-md w-full"
                      >
                        <FaSignOutAlt className="w-4 h-4" />
                        <span>Sign Out</span>
                      </button>
                    </div>
                  </div>
                )}
              </div>
            </div>
          </div>
        </nav>
      )}
    </>
  );
}
