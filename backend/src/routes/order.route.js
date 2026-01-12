import express from "express"
import { getOrders } from "../controllers/order.controller.js"  

let orderRoute = express.Router()

// Define order-related routes here
orderRoute.get("/", getOrders)

export default orderRoute