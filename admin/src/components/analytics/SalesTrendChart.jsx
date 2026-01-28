import React from "react";
import {
  ComposedChart,
  Area,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer
} from "recharts";

export default function SalesTrendChart({ data }) {
  return (
    <div className="bg-white p-6 rounded-2xl border border-gray-100 shadow-sm">
      <div className="flex justify-between items-center mb-6">
        <div>
          <h3 className="text-lg font-bold text-gray-800">Sales & Order Volume Trends</h3>
        
        </div>
      </div>
      <div className="h-80 w-full">
        <ResponsiveContainer width="100%" height="100%">
          <ComposedChart data={data || []}>
            <defs>
              <linearGradient id="colorSales" x1="0" y1="0" x2="0" y2="1">
                <stop offset="5%" stopColor="#3B82F6" stopOpacity={0.2}/>
                <stop offset="95%" stopColor="#3B82F6" stopOpacity={0}/>
              </linearGradient>
            </defs>
            <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#f3f4f6" />
            <XAxis dataKey="date" axisLine={false} tickLine={false} tick={{fill: '#9CA3AF', fontSize: 12}} dy={10} />
            <YAxis yAxisId="left" axisLine={false} tickLine={false} tick={{fill: '#9CA3AF', fontSize: 12}} />
            <YAxis yAxisId="right" orientation="right" axisLine={false} tickLine={false} tick={{fill: '#9CA3AF', fontSize: 12}} />
            <Tooltip contentStyle={{borderRadius: '8px', border: 'none', boxShadow: '0 4px 6px -1px rgb(0 0 0 / 0.1)'}} />
            <Legend />
            <Area yAxisId="left" type="linear" dataKey="sales" name="Revenue (₹)" stroke="#3B82F6" fillOpacity={1} fill="url(#colorSales)" strokeWidth={3} />
            <Line yAxisId="right" type="linear" dataKey="orders" name="Order Count" stroke="#10B981" strokeWidth={3} dot={{r: 4}} />
          </ComposedChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
}
