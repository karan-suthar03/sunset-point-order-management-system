import express from "express"
import cors from "cors"
import orderRoute from "./routes/order.route.js"
import dishRoute from "./routes/dish.route.js"

let app = express()
app.use(cors())
app.use(express.json())


app.use("/orders", orderRoute)  
app.use("/dishes", dishRoute)



export default app