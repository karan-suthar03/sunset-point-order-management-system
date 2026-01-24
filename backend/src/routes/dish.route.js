import express from "express";
import { getDishes, getDishById, getCategories } from "../controllers/dish.controller.js";  

let dishRoute = express.Router();
dishRoute.get("/", getDishes);
dishRoute.get("/categories", getCategories);
dishRoute.get("/:id", getDishById);


export default dishRoute;