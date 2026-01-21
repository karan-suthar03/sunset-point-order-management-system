import express from "express"
import { closeOrder, getOrders, postOrder } from "../controllers/order.controller.js"  

let orderRoute = express.Router()

// Define order-related routes here
orderRoute.get("/", getOrders);
orderRoute.post("/", postOrder);
orderRoute.put("/close", closeOrder);

export default orderRoute