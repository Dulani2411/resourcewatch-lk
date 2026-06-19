import axios from 'axios';

const API_BASE = 'http://127.0.0.1:49991/api';

const api = axios.create({
  baseURL: API_BASE,
  timeout: 10000,
});

// Weather
export const getLatestWeather = () => api.get('/weather/latest');
export const getForecast = () => api.get('/weather/forecast');

// Water Risk
export const getWaterRisk = () => api.get('/water/risk');

// Power Stress
export const getPowerStress = () => api.get('/power/stress');

// Reports
export const getReports = () => api.get('/reports');
export const getReportsByDistrict = (district) => api.get(`/reports?district=${district}`);
export const submitReport = (report) => api.post('/reports', report);
export const getDistrictSummary = () => api.get('/reports/summary');

// AI
export const askAI = (question) => api.post('/ai/ask', { question });