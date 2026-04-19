import request from '@/utils/request'

export const getMyReservations = () => request.get('/reservations')

export const createReservation = (bookId) => request.post(`/reservations/${bookId}`)

export const cancelReservation = (id) => request.delete(`/reservations/${id}`)

export const pickupReservation = (id) => request.post(`/reservations/${id}/pickup`)