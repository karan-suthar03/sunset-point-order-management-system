import express from "express";
import { getDishes } from "../controllers/dish.controller.js";  

let dishRoute = express.Router();
dishRoute.get("/", getDishes);


export default dishRoute;