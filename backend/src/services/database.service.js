import pool from "../db.js";   

async function getDishes() {   
    let query = `SELECT * FROM dishes`;
    let result = await pool.query(query);
    return result.rows;
} 

export default { getDishes };
