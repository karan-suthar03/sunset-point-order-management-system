import dbService from "../services/database.service.js";

async function getDishes(req, res) {  
    try {
        let rows = await dbService.getDishes();
        let result = {}

        for (let row of rows) {
            if(!result[row.category]){
                result[row.category] = [{
                    id: row.dish_id,
                    name: row.dish_name,
                    price: row.price
                }]
            }else{
                result[row.category].push({
                    id: row.dish_id,
                    name: row.dish_name,
                    price: row.price
                })
            }
        }
        return res.status(200).send(result);
    } catch (error) {
        return res.status(500).send({message: "Internal Server Error"})
    }
}

async function getDishById(req,res) {
    try{
        let dishId = req.params.id;
        let dish = await dbService.getDishById(dishId);
        return res.status(200).json(dish);
    }catch(error){
        return res.status(500).send({message: "Internal Server Error"})
    }
}

async function getCategories(req,res) {
    try{
        console.log("Fetching dish categories...");
        let categories = await dbService.getDishCategories();
        console.log("Categories fetched:", categories);
        return res.status(200).json(categories);
    }catch(error){
        return res.status(500).send({message: "Internal Server Error"})
    }
}

export { getDishes, getDishById, getCategories };