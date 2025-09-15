export const fetchSvgCharts = async (token, selectedComparisonParameters, selectedDailyTrendParameter, selectedMonthlyAverageParameter, selectedTextFrequencyParameter) => {
    const endpoints = {
        comparison: `http://localhost:8000/charts/${selectedComparisonParameters[0]}/${selectedComparisonParameters[1]}/comparison`,
        dailyTrend: `http://localhost:8000/charts/${selectedDailyTrendParameter}/daily-trend`,
        monthlyAverageBar: `http://localhost:8000/charts/${selectedMonthlyAverageParameter}/monthly-average-bar`,
        textFrequency: `http://localhost:8000/charts/${selectedTextFrequencyParameter}/text-frequency`,
    };

    try {
        const requests = Object.entries(endpoints).map(async ([key, url]) => {
            const response = await fetch(url, {
                headers: { 'Authorization': `Bearer ${token}` },
            });

            if (!response.ok) {
                console.error(`Error fetching ${key} from ${url}:`, response.statusText);
                return { [key]: `Ошибка при загрузке ${key}` };
            }

            return { [key]: await response.text() };
        });

        const results = await Promise.all(requests);

        return results.reduce((acc, curr) => ({ ...acc, ...curr }), {});
    } catch (error) {
        console.error("Error fetching charts:", error);
        return { error: 'Ошибка при запросе SVG' };
    }
};
