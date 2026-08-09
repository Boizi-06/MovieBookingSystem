import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';
import Layout from './components/Layout';

// Import Pages
import Home from './pages/Home';
import Login from './pages/Login';
import Register from './pages/Register';
import ForgotPassword from './pages/ForgotPassword';
import ResetPassword from './pages/ResetPassword';
import VerifyEmail from './pages/VerifyEmail';
import MovieDetail from './pages/MovieDetail';
import TermsOfService from './pages/TermsOfService';
import PrivacyPolicy from './pages/PrivacyPolicy';
import ContactSupport from './pages/ContactSupport';
import Profile from './pages/Profile';
import MyBookings from './pages/MyBookings';
import SelectSeats from './pages/SelectSeats';
import PaymentCheckout from './pages/PaymentCheckout';
import BookingSuccess from './pages/BookingSuccess';
import AdminDashboard from './pages/AdminDashboard';
import ManageMovies from './pages/admin/ManageMovies';
import ManageCinemas from './pages/admin/ManageCinemas';
import ManageShowtimes from './pages/admin/ManageShowtimes';
import ManageBookings from './pages/admin/ManageBookings';
import ManageUsers from './pages/admin/ManageUsers';

import './App.css';

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Layout>
          <Routes>
            {/* Public Routes */}
            <Route path="/" element={<Home />} />
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/forgot-password" element={<ForgotPassword />} />
            <Route path="/reset-password" element={<ResetPassword />} />
            <Route path="/verify-email" element={<VerifyEmail />} />
            <Route path="/movies/:id" element={<MovieDetail />} />
            <Route path="/terms" element={<TermsOfService />} />
            <Route path="/privacy" element={<PrivacyPolicy />} />
            <Route path="/contact" element={<ContactSupport />} />
            
            {/* Protected Customer/Admin Routes */}
            <Route path="/profile" element={
              <ProtectedRoute>
                <Profile />
              </ProtectedRoute>
            } />

            <Route path="/my-bookings" element={
              <ProtectedRoute>
                <MyBookings />
              </ProtectedRoute>
            } />

            <Route path="/booking/seats/:showtimeId" element={
              <ProtectedRoute>
                <SelectSeats />
              </ProtectedRoute>
            } />

            <Route path="/booking/payment/:bookingId" element={
              <ProtectedRoute>
                <PaymentCheckout />
              </ProtectedRoute>
            } />

            <Route path="/booking/success/:bookingCode" element={
              <ProtectedRoute>
                <BookingSuccess />
              </ProtectedRoute>
            } />

            {/* Protected Admin Only Routes */}
            <Route path="/admin/dashboard" element={
              <ProtectedRoute allowedRoles={['ADMIN']}>
                <AdminDashboard />
              </ProtectedRoute>
            } />

            <Route path="/admin/movies" element={
              <ProtectedRoute allowedRoles={['ADMIN']}>
                <ManageMovies />
              </ProtectedRoute>
            } />

            <Route path="/admin/cinemas" element={
              <ProtectedRoute allowedRoles={['ADMIN']}>
                <ManageCinemas />
              </ProtectedRoute>
            } />

            <Route path="/admin/showtimes" element={
              <ProtectedRoute allowedRoles={['ADMIN']}>
                <ManageShowtimes />
              </ProtectedRoute>
            } />

            <Route path="/admin/bookings" element={
              <ProtectedRoute allowedRoles={['ADMIN']}>
                <ManageBookings />
              </ProtectedRoute>
            } />

            <Route path="/admin/users" element={
              <ProtectedRoute allowedRoles={['ADMIN']}>
                <ManageUsers />
              </ProtectedRoute>
            } />

            {/* Redirect all unmatched routes to home */}
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </Layout>
      </BrowserRouter>
    </AuthProvider>
  );
}
