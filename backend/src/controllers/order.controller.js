import pool from "../db.js";


function getOrders(req, res) {
  const query = `
    SELECT 
      o.order_id,
      o.order_tag,
      o.created_at,
      o.order_status,
      o.is_payment_done,
      o.order_total,

      oi.order_item_id,
      oi.quantity,
      oi.item_status,
      oi.price_snapshot AS price,
      oi.dish_name_snapshot AS dish_name,

      d.dish_id,
      d.category
    FROM orders o
    LEFT JOIN order_items oi
      ON o.order_id = oi.order_id
    LEFT JOIN dishes d
      ON oi.dish_id = d.dish_id
    ORDER BY o.created_at;
  `;

  pool.query(query)
    .then(result => {
      const ordersMap = new Map();

      result.rows.forEach(row => {
        // Create order once
        if (!ordersMap.has(row.order_id)) {
          ordersMap.set(row.order_id, {
            id: row.order_id,
            items: [],
            tag: row.order_tag,
            createdAt: row.created_at,
            status: row.order_status,
            paymentDone: row.is_payment_done,
            orderTotal: row.order_total
          });
        }

        // Add item only if it exists
        if (row.order_item_id) {
          ordersMap.get(row.order_id).items.push({
            id: row.order_item_id,
            quantity: row.quantity,
            status: row.item_status,
            name: row.dish_name,
            category: row.category,
            price: row.price
          });
        }
      });

      res.status(200).send([...ordersMap.values()]);
    })
    .catch(err => {
      console.error("Error fetching orders:", err);
      res.status(500).send({ error: "Failed to fetch orders" });
    });
}


async function postOrder(req, res) {
    const { items, tag } = req.body;

    if (!items || !Array.isArray(items) || items.length === 0) {
        return res.status(400).json({ error: "Invalid items in request body" });
    }

    const client = await pool.connect();

    try {
        await client.query("BEGIN");

        const orderResult = await client.query(
            `INSERT INTO orders (is_payment_done, order_status, order_tag)
             VALUES ($1, $2, $3)
             RETURNING order_id`,
            [false, "OPEN", tag]
        );

        const orderId = orderResult.rows[0].order_id;

        const dishIds = items.map(i => i.id);

        const dishesResult = await client.query(
            `SELECT dish_id, dish_name, price
             FROM dishes
             WHERE dish_id = ANY($1::int[])`,
            [dishIds]
        );

        if (dishesResult.rows.length !== dishIds.length) {
            throw new Error("One or more dishes not found");
        }

        const dishMap = new Map();
        dishesResult.rows.forEach(d =>
            dishMap.set(d.dish_id, d)
        );
        const values = [];
        const placeholders = [];

        items.forEach((item, index) => {
            const dish = dishMap.get(item.id);

            if (!dish) {
                throw new Error(`Dish with id ${item.id} not found`);
            }

            const base = index * 5;
            placeholders.push(
                `($${base + 1}, $${base + 2}, $${base + 3}, $${base + 4}, $${base + 5})`
            );

            values.push(
                orderId,
                item.id,
                item.quantity,
                dish.dish_name,
                dish.price
            );
        });

        await client.query(
            `INSERT INTO order_items
             (order_id, dish_id, quantity, dish_name_snapshot, price_snapshot)
             VALUES ${placeholders.join(", ")}`,
            values
        );

        await client.query("COMMIT");

        res.status(201).json({
            message: "Order created successfully",
            orderId
        });

    } catch (err) {
        await client.query("ROLLBACK");
        console.error("Transaction failed:", err.message);

        res.status(500).json({
            error: "Failed to create order",
            details: err.message
        });

    } finally {
        client.release();
    }
}

async function closeOrder(req,res) {
  let orderId = req.query.id;
  try{
    let query = `UPDATE orders SET order_status='CLOSED', is_payment_done=true WHERE order_id=$1 RETURNING *`;
    let result = await pool.query(query, [orderId]);
    if(result.rowCount === 0){
      return res.status(404).json({error: "Order not found"});
    }
    res.status(200).json({message: "Order closed successfully", order: result.rows[0]});
  } catch(err){
    console.error("Error closing order:", err);
    res.status(500).json({error: "Failed to close order"});
  }
}


export { getOrders, postOrder, closeOrder };