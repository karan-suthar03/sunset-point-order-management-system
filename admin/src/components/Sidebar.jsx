import React, { useState } from "react";
import { NavLink, useNavigate } from "react-router-dom";
import {
  LayoutDashboard,
  TrendingUp,
  ShoppingBag,
  Users,
  UtensilsCrossed,
  LogOut,
  Package,
  ChefHat,
  Database
} from "lucide-react";

function SidebarItem({ to, icon: Icon, label }) {
  return (
    <NavLink
      to={to}
      className={({ isActive }) => `
        w-full flex items-center gap-3 px-4 py-3 rounded-xl transition-all duration-200 font-medium
        ${isActive
          ? "bg-blue-600 text-white shadow-lg shadow-blue-200"
          : "text-gray-500 hover:bg-gray-50 hover:text-gray-900"}
      `}
    >
      <Icon size={20} />
      <span>{label}</span>
    </NavLink>
  );
}

export default function Sidebar() {
  const [isBackingUp, setIsBackingUp] = useState(false);
  const [backupMessage, setBackupMessage] = useState("");

  const handleBackup = async () => {
    if (!window.NativeApi) {
      setBackupMessage("Backup is only available on Android");
      setTimeout(() => setBackupMessage(""), 3000);
      return;
    }

    setIsBackingUp(true);
    setBackupMessage("");

    try {
      const requestId = `backup_${Date.now()}`;
      
      const backupPromise = new Promise((resolve, reject) => {
        window.__nativeResolve = (reqId, result) => {
          if (reqId === requestId) {
            try {
              const parsed = JSON.parse(result);
              if (parsed.success) {
                resolve(parsed);
              } else {
                reject(new Error(parsed.message || "Backup failed"));
              }
            } catch (e) {
              reject(new Error("Invalid response from backup"));
            }
          }
        };
      });

      window.NativeApi.backupDatabase(requestId);
      
      const result = await backupPromise;
      setBackupMessage(`✓ Backup saved: ${result.filename}`);
      setTimeout(() => setBackupMessage(""), 5000);
    } catch (error) {
      setBackupMessage(`✗ ${error.message}`);
      setTimeout(() => setBackupMessage(""), 5000);
    } finally {
      setIsBackingUp(false);
    }
  };

  return (
    <aside className="w-64 bg-white border-r border-gray-200 hidden lg:flex flex-col fixed h-full z-20">
      <div className="p-6 border-b border-gray-100 flex items-center gap-3">
        <div className="bg-blue-600 p-2 rounded-lg">
          <ChefHat className="text-white" size={24} />
        </div>
        <div>
          <h1 className="font-bold text-lg tracking-tight">RestoAdmin</h1>
        </div>
      </div>

      <nav className="flex-1 p-4 space-y-2 overflow-y-auto">
        <div className="text-xs font-bold text-gray-400 uppercase tracking-wider mb-2 px-4 mt-2">
          Overview
        </div>
        <SidebarItem to="/analytics" icon={TrendingUp} label="Analytics" />

        <div className="text-xs font-bold text-gray-400 uppercase tracking-wider mb-2 px-4 mt-6">
          Management
        </div>
        <SidebarItem to="/orders" icon={ShoppingBag} label="Orders History" />
        <SidebarItem to="/menu" icon={UtensilsCrossed} label="Menu Items" />
        {/* Inventory is disabled on Android - only available on web */}
        {!window.NativeApi && <SidebarItem to="/inventory" icon={Package} label="Inventory" />}
      </nav>

      {/* Backup Section - Only visible on Android */}
      {window.NativeApi && (
        <div className="p-4 border-t border-gray-100">
          <button
            onClick={handleBackup}
            disabled={isBackingUp}
            className={`
              w-full flex items-center gap-3 px-4 py-3 rounded-xl transition-all duration-200 font-medium
              ${isBackingUp 
                ? "bg-gray-300 text-gray-500 cursor-not-allowed" 
                : "bg-blue-600 text-white hover:bg-blue-700 shadow-lg shadow-blue-200"}
            `}
          >
            <Database size={20} />
            <span>{isBackingUp ? "Backing up..." : "Backup Database"}</span>
          </button>
          {backupMessage && (
            <div className={`mt-2 px-3 py-2 rounded-lg text-xs font-medium ${
              backupMessage.startsWith("✓") 
                ? "bg-green-50 text-green-700" 
                : "bg-red-50 text-red-700"
            }`}>
              {backupMessage}
            </div>
          )}
        </div>
      )}
    </aside>
  );
}
