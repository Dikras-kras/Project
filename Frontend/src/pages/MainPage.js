import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Select, Card, Typography } from 'antd';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import { fetchSvgCharts } from '../api/fetchSvg';
import { fetchRealtimeData } from '../api/fetchRealtimeData';

const { Option } = Select;
const { Title } = Typography;

const MainPage = () => {
  const [svgComparisonContent, setSvgComparisonContent] = useState(null);
  const [svgDailyTrendContent, setSvgDailyTrendContent] = useState(null);
  const [svgMonthlyAverageBarContent, setSvgMonthlyAverageBarContent] = useState(null);
  const [svgTextFrequencyContent, setSvgTextFrequencyContent] = useState(null);

  const [selectedComparisonParameters, setSelectedComparisonParameters] = useState(['rainfall', 'sunshine']);
  const [selectedDailyTrendParameter, setSelectedDailyTrendParameter] = useState('rainfall');
  const [selectedMonthlyAverageParameter, setSelectedMonthlyAverageParameter] = useState('rainfall');
  const [selectedTextFrequencyParameter, setSelectedTextFrequencyParameter] = useState('windgustdir');

  const [data, setData] = useState([]);
  const [frequency, setFrequency] = useState(10); // Частота обновления в секундах
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const token = localStorage.getItem('token');

  // 🔹 Все доступные параметры
  const weatherParameters = [
    { value: "mintemp", label: "signal a" },
    { value: "maxtemp", label: "signal b" },
    { value: "rainfall", label: "signal c" },
    { value: "evaporation", label: "signal d" },
    { value: "sunshine", label: "signal e" },
    { value: "windgustspeed", label: "signal f" },
    { value: "windspeed9am", label: "signal g" },
    { value: "windspeed3pm", label: "signal h" },
    { value: "humidity9am", label: "signal i" },
    { value: "humidity3pm", label: "signal j" },
    { value: "pressure9am", label: "signal k" },
    { value: "pressure3pm", label: "signal l" },
    { value: "cloud9am", label: "signal m" },
    { value: "cloud3pm", label: "signal n" },
    { value: "temp9am", label: "signal o" },
    { value: "temp3pm", label: "signal p" },
  ];

  const textParameters = [
    { value: "windgustdir", label: "signal s" },
    { value: "winddir9am", label: "signal t" },
    { value: "winddir3pm", label: "signal u" },
    { value: "raintoday", label: "signal q" },
    { value: "raintomorrow", label: "signal y" },
  ];

  const getLabel = (value) => {
    const all = [...weatherParameters, ...textParameters];
    const found = all.find(p => p.value === value);
    return found ? found.label : value;
  };

  useEffect(() => {
    if (!token) {
      navigate('/');
    } else {
      fetchCharts();
      const fetchData = async () => {
        const fetchedData = await fetchRealtimeData(token);
        if (fetchedData) {
          setData(fetchedData);
        } else {
          setError('Ошибка при получении данных');
        }
      };

      fetchData(); // Изначальный запрос данных
      const interval = setInterval(fetchData, frequency * 1000);
      return () => clearInterval(interval);
    }
  }, [
    token,
    selectedComparisonParameters,
    selectedDailyTrendParameter,
    selectedMonthlyAverageParameter,
    selectedTextFrequencyParameter,
    frequency,
  ]);

  const fetchCharts = async () => {
    if (loading) return;
    setLoading(true);

    const results = await fetchSvgCharts(
      token,
      selectedComparisonParameters,
      selectedDailyTrendParameter,
      selectedMonthlyAverageParameter,
      selectedTextFrequencyParameter
    );

    if (results.error) {
      setError(results.error);
    } else {
      setSvgComparisonContent(results.comparison || null);
      setSvgDailyTrendContent(results.dailyTrend || null);
      setSvgMonthlyAverageBarContent(results.monthlyAverageBar || null);
      setSvgTextFrequencyContent(results.textFrequency || null);
    }

    setLoading(false);
  };

  // 🔹 Обновлённая функция для замены текста в SVG
  const replaceSvgContent = (svgContent) => {
    if (!svgContent) return '';

    const labelMap = {};
    [...weatherParameters, ...textParameters].forEach(({ value, label }) => {
      labelMap[value] = label;
    });

    let updated = svgContent.replace(
      /<svg([^>]*)>/,
      '<svg$1 width="100%" height="100%" viewBox="0 0 800 600">'
    );

    // Заменяем английские подписи на label
    Object.entries(labelMap).forEach(([key, label]) => {
      const regex = new RegExp(key, "gi");
      updated = updated.replace(regex, label);
    });

    return updated;
  };

  const WeatherSelect = ({ value, onChange }) => (
    <Select value={value} onChange={onChange} style={{ width: '300px', marginBottom: '10px' }}>
      {weatherParameters.map(({ value, label }) => (
        <Option key={value} value={value}>{label}</Option>
      ))}
    </Select>
  );

  const TextSelect = ({ value, onChange }) => (
    <Select value={value} onChange={onChange} style={{ width: '300px', marginBottom: '10px' }}>
      {textParameters.map(({ value, label }) => (
        <Option key={value} value={value}>{label}</Option>
      ))}
    </Select>
  );

  const handleFrequencyChange = (value) => setFrequency(value);

  // 🔹 Пример данных для Recharts
  const chartData = data.map((item) => ({
    timestamp: item.timestamp,
    value_2: item.temperature,
    value_1: item.humidity,
    value_3: item.windSpeed,
  }));

  const svgStyle = {
    width: '100%',
    height: '100%',
    display: 'block',
    objectFit: 'contain',
  };

  return (
    <div style={{ padding: '20px', maxWidth: '1000px', margin: '0 auto' }}>
      {error && <Title type="danger" level={4}>{error}</Title>}

      {/* 🔹 Графики в реальном времени */}
      <Card title="Графики в реальном времени" style={{ marginBottom: '20px' }}>
        <Select
          value={frequency}
          onChange={handleFrequencyChange}
          style={{ width: '200px', marginBottom: '20px' }}
        >
          <Option value={5}>5 секунд</Option>
          <Option value={10}>10 секунд</Option>
          <Option value={20}>20 секунд</Option>
          <Option value={30}>30 секунд</Option>
        </Select>

        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
          <ResponsiveContainer width="100%" height={300}>
            <LineChart data={chartData}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="timestamp" />
              <YAxis />
              <Tooltip />
              <Legend />
              <Line type="monotone" dataKey="value_2" name="Ось X" stroke="#8884d8" />
              <Line type="monotone" dataKey="value_1" name="Ось Y" stroke="#82ca9d" />
              <Line type="monotone" dataKey="value_3" name="Ось Z" stroke="#ff7300" />
            </LineChart>
          </ResponsiveContainer>
        </div>
      </Card>

      {/* 🔹 Остальные графики (SVG) */}
      <Card title="График для сравнения двух параметров" style={{ marginBottom: '20px' }}>
        <WeatherSelect value={selectedComparisonParameters[0]} onChange={(value) => setSelectedComparisonParameters([value, selectedComparisonParameters[1]])} />
        <WeatherSelect value={selectedComparisonParameters[1]} onChange={(value) => setSelectedComparisonParameters([selectedComparisonParameters[0], value])} />
        <div style={svgStyle} dangerouslySetInnerHTML={{ __html: replaceSvgContent(svgComparisonContent) }} />
      </Card>

      <Card title="График тренда по дням" style={{ marginBottom: '20px' }}>
        <WeatherSelect value={selectedDailyTrendParameter} onChange={setSelectedDailyTrendParameter} />
        <div style={svgStyle} dangerouslySetInnerHTML={{ __html: replaceSvgContent(svgDailyTrendContent) }} />
      </Card>

      <Card title="Столбчатая диаграмма с усреднёнными значениями по месяцам" style={{ marginBottom: '20px' }}>
        <WeatherSelect value={selectedMonthlyAverageParameter} onChange={setSelectedMonthlyAverageParameter} />
        <div style={svgStyle} dangerouslySetInnerHTML={{ __html: replaceSvgContent(svgMonthlyAverageBarContent) }} />
      </Card>

      <Card title="График частоты текстовых параметров">
        <TextSelect value={selectedTextFrequencyParameter} onChange={setSelectedTextFrequencyParameter} />
        <div style={svgStyle} dangerouslySetInnerHTML={{ __html: replaceSvgContent(svgTextFrequencyContent) }} />
      </Card>
    </div>
  );
};

export default MainPage;
