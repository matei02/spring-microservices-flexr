import { Geist } from "next/font/google";
import "./globals.css";
import Navigation from './components/layout/Navigation';
import Auth0ProviderWrapper from './components/auth/Auth0Provider';
import ProtectedRoute from './components/auth/ProtectedRoute';
import { UserProvider } from './context/UserContext';

const geistSans = Geist({
  variable: "--font-geist-sans",
  subsets: ["latin"],
});

export const metadata = {
  title: 'FlexR - Share Your Achievements',
  description: 'A social media platform for sharing your achievements and milestones',
}

export default function RootLayout({ children }) {
  return (
    <html lang="en" className={geistSans.variable}>
      <body className="bg-black min-h-screen">
        <Auth0ProviderWrapper>
          <UserProvider>
            <Navigation />
            <ProtectedRoute>
              <main className="pt-20 pb-24 px-4">
                {children}
              </main>
            </ProtectedRoute>
          </UserProvider>
        </Auth0ProviderWrapper>
      </body>
    </html>
  );
}
