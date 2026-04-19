import request from '@/utils/request'

export const submitDonation = (data) => request.post('/donations', data)

export const getMyDonations = () => request.get('/donations')

export const getAllDonations = (params) => request.get('/donations/all', { params })

export const reviewDonation = (id, data) => request.put(`/donations/${id}`, data)