import React, { useState, useMemo, useEffect } from "react";
import { getAnalytics, getDishPerformance } from "../API/analytics.js";

// Import analytics components
import AnalyticsControlBar from "../components/analytics/AnalyticsControlBar";
import KPICards from "../components/analytics/KPICards";
import SalesTrendChart from "../components/analytics/SalesTrendChart";
import HourlyTrafficChart from "../components/analytics/HourlyTrafficChart";
import CategoryPerformanceWidget from "../components/analytics/CategoryPerformanceWidget";
import OrderSizeBehavior from "../components/analytics/OrderSizeBehavior";
import DishPerformanceTable from "../components/analytics/DishPerformanceTable";
import CategoryModal from "../components/analytics/CategoryModal";
import DishModal from "../components/analytics/DishModal";

export default function Analytics() {
  const [dateRange, setDateRange] = useState("Last 7 Days");
  const [dishFilter, setDishFilter] = useState("revenue"); // 'revenue', 'quantity'
  const [dateSelection, setDateSelection] = useState({ start: null, end: null });
  
  // Modal States
  const [showCategoryModal, setShowCategoryModal] = useState(false);
  const [showDishModal, setShowDishModal] = useState(false);

  const [data, setData] = useState({
    totalRevenue: 0, 
    totalOrders: 0, 
    averageOrderValue: 0, 
    averageOrderPerDay: 0, 
    avgItemsPerOrder: 0, 
    salesTrend: [], 
    hourlyTrafficAvg: [], 
    categoryPerformance: [], 
    orderSize: []
  });  

  const [dishData, setDishData] = useState([]);

  useMemo(() => {
    (async () => {
      if (dateRange === "Custom Range" && (!dateSelection.start || !dateSelection.end)) {
        return; // Don't fetch if custom range is selected but dates aren't picked
      }
      setDishData([]);
      let result;
      if (dateRange === "Custom Range") {
        let start = dateSelection.start;
        let end = dateSelection.end;
        result = await getDishPerformance({ start, end }, dishFilter, 100);
      } else {
        result = await getDishPerformance(dateRange, dishFilter, 100);
      }
      setDishData(result);
    })();
  }, [dishFilter, dateRange, dateSelection]); // Added dateRange and dateSelection dependencies

  useEffect(() => {
    if (dateRange === "Custom Range" && (!dateSelection.start || !dateSelection.end)) {
      return; // Don't fetch if custom range is selected but dates aren't picked
    }
    fetchData();
  }, [dateRange, dateSelection]); // Fetch when dateRange or dateSelection changes

  useEffect(() => {
    // Prevent body scroll when modal is open
    if (showCategoryModal || showDishModal) {
      document.body.style.overflow = "hidden";
    } else {
      document.body.style.overflow = "unset";
    }

    // Cleanup on unmount
    return () => {
      document.body.style.overflow = "unset";
    };
  }, [showCategoryModal, showDishModal]);

  async function fetchData() {
    try {
      setData({
    totalRevenue: 0, 
    totalOrders: 0, 
    averageOrderValue: 0, 
    averageOrderPerDay: 0, 
    avgItemsPerOrder: 0, 
    salesTrend: [], 
    hourlyTrafficAvg: [], 
    categoryPerformance: [], 
    orderSize: []
  })
      let result;
      if (dateRange === "Custom Range" && dateSelection.start && dateSelection.end) {
        let start = dateSelection.start;
        let end = dateSelection.end;
        result = await getAnalytics({ start, end });
      } else if (dateRange !== "Custom Range") {
        result = await getAnalytics(dateRange);
      } else {
        return; // Don't fetch if custom range is selected but dates aren't picked
      }

      if(result) {
        setData({
          totalRevenue: result.totalRevenue,
          totalOrders: result.totalOrders,
          averageOrderValue: result.avgOrderValue,
          averageOrderPerDay: result.avgOrdersPerDay,
          avgItemsPerOrder: result.avgNumberOfItemsPerOrder,
          salesTrend: result.salesTrendData,
          hourlyTrafficAvg: result.hourlyRushData,
          categoryPerformance: result.categoryPerformanceData,
          orderSize: result.orderSizeData
        });
      }
    } catch (error) {
      console.error("Error fetching analytics data:", error);
    }
  }

  return (
    <div className="min-h-screen bg-gray-50/50 pb-10 font-sans text-slate-900">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 py-6 space-y-6">
        
        {/* --- CONTROL BAR --- */}
        <AnalyticsControlBar 
          dateRange={dateRange}
          setDateRange={setDateRange}
          dateSelection={dateSelection}
          setDateSelection={setDateSelection}
          onApply={fetchData}
        />

        {/* --- KPI CARDS --- */}
        <KPICards data={data} />

        {/* --- TREND CHART --- */}
        <SalesTrendChart data={data.salesTrend} />

        {/* --- SPLIT ROW 1: Hourly & Category --- */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <HourlyTrafficChart data={data.hourlyTrafficAvg} />
          <CategoryPerformanceWidget 
            data={data.categoryPerformance} 
            onViewAll={() => setShowCategoryModal(true)}
          />
        </div>

        {/* --- SPLIT ROW 2: Order Size & Dish Details --- */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <OrderSizeBehavior data={data.orderSize} />
          <DishPerformanceTable 
            dishData={dishData}
            dishFilter={dishFilter}
            setDishFilter={setDishFilter}
            onViewAll={() => setShowDishModal(true)}
          />
        </div>

      </div>

      <CategoryModal 
        isOpen={showCategoryModal}
        onClose={() => setShowCategoryModal(false)}
        range={dateRange != "Custom Range" ? dateRange : dateSelection}
      />

      <DishModal 
        isOpen={showDishModal}
        onClose={() => setShowDishModal(false)}
        dishFilter={dishFilter}
        range={dateRange != "Custom Range" ? dateRange : dateSelection}
      />

    </div>
  );
}