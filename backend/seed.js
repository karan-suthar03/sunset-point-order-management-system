import pool from "./src/db.js";

let dishes = [
  // Hot Beverages
  { dish_name: "Tea", category: "Hot Beverage", price: 20 },
  { dish_name: "Green Tea", category: "Hot Beverage", price: 30 },
  { dish_name: "Black Tea", category: "Hot Beverage", price: 20 },
  { dish_name: "Hot Coffee", category: "Hot Beverage", price: 30 },
  { dish_name: "Black Coffee", category: "Hot Beverage", price: 25 },
  { dish_name: "Plain Hot Chocolate", category: "Hot Beverage", price: 40 },

  // Cold Coffee
  { dish_name: "Cold Coffee", category: "Cold Coffee", price: 50 },
  { dish_name: "Cold Coffee with Crush", category: "Cold Coffee", price: 70 },

  // Refreshers
  { dish_name: "Lemon Ice Tea", category: "Refresher", price: 50 },
  { dish_name: "Peach Ice Tea", category: "Refresher", price: 50 },
  { dish_name: "Mint Mojito", category: "Refresher", price: 80 },
  { dish_name: "Green Apple Mojito", category: "Refresher", price: 80 },

  // Smoothies
  { dish_name: "Blue Berry Smoothie", category: "Smoothie", price: 110 },
  { dish_name: "Strawberry Smoothie", category: "Smoothie", price: 110 },
  { dish_name: "Mango Smoothie", category: "Smoothie", price: 110 },
  { dish_name: "Oreo Smoothie", category: "Smoothie", price: 120 },
  { dish_name: "Dark Chocolate Smoothie", category: "Smoothie", price: 130 },

  // Shakes
  { dish_name: "Kit Kat Shake", category: "Shake", price: 110 },
  { dish_name: "Java Choco Chip Shake", category: "Shake", price: 120 },
  { dish_name: "Brownie Shake", category: "Shake", price: 130 },
  { dish_name: "Nutella Shake", category: "Shake", price: 140 },
  { dish_name: "Oreo Shake", category: "Shake", price: 100 },
  { dish_name: "Butter Scotch Shake", category: "Shake", price: 100 },
  { dish_name: "Rose Shake", category: "Shake", price: 90 },

  // Sandwich
  { dish_name: "Green Chatni Sandwich", category: "Sandwich", price: 60 },
  { dish_name: "Triple Cheese Sandwich", category: "Sandwich", price: 80 },
  { dish_name: "Chocolate Sandwich", category: "Sandwich", price: 80 },
  { dish_name: "Bombay Masala Sandwich", category: "Sandwich", price: 80 },
  { dish_name: "Classic Club Sandwich", category: "Sandwich", price: 100 },
  { dish_name: "Paneer Sandwich", category: "Sandwich", price: 120 },
  { dish_name: "Mexican Sandwich", category: "Sandwich", price: 130 },
  { dish_name: "Cheese Corn Sandwich", category: "Sandwich", price: 110 },
  { dish_name: "Extra Spicy Sandwich", category: "Sandwich", price: 120 },

  // Maggi
  { dish_name: "Plain Maggi", category: "Maggi", price: 50 },
  { dish_name: "Masala Maggi", category: "Maggi", price: 60 },
  { dish_name: "Cheese Masala Maggi", category: "Maggi", price: 70 },
  { dish_name: "Italian Maggi", category: "Maggi", price: 90 },
  { dish_name: "Peri Peri Cheese Maggi", category: "Maggi", price: 80 },
  { dish_name: "Cheese Corn Maggi", category: "Maggi", price: 80 },

  // Pasta
  { dish_name: "Alfredo Pasta (White)", category: "Pasta", price: 160 },
  { dish_name: "Arrabiata Pasta (Red)", category: "Pasta", price: 160 },
  { dish_name: "Pink Pasta (Red + White)", category: "Pasta", price: 180 },
  { dish_name: "Indian Pasta (All Veggies)", category: "Pasta", price: 120 },

  // Fries
  { dish_name: "Salted Fries", category: "Fries", price: 60 },
  { dish_name: "Peri Peri Fries", category: "Fries", price: 80 },
  { dish_name: "Cheese Peri Peri Fries", category: "Fries", price: 100 },
  { dish_name: "Chipotle Fries", category: "Fries", price: 120 },
  { dish_name: "Cheese Corn Balls (6 pcs)", category: "Fries", price: 120 },

  // Pizza
  { dish_name: "Margarita Pizza", category: "Pizza", price: 100 },
  { dish_name: "Cheese Chilli Toast", category: "Pizza", price: 80 },
  { dish_name: "Cheese Burst Pizza", category: "Pizza", price: 100 },
  { dish_name: "Corn Cheese Pizza", category: "Pizza", price: 110 },
  { dish_name: "Mexican Cheese Pizza", category: "Pizza", price: 140 },
  { dish_name: "Tandoori Paneer Pizza", category: "Pizza", price: 150 },
  { dish_name: "Mix Veg Cheese Pizza", category: "Pizza", price: 160 },
  { dish_name: "Pasta Pizza", category: "Pizza", price: 180 },
  { dish_name: "Special Pizza", category: "Pizza", price: 200 },

  // Burger
  { dish_name: "Crispy Veg Burger", category: "Burger", price: 80 },
  { dish_name: "Crispy Veg Cheese Burger", category: "Burger", price: 100 },
  { dish_name: "Crispy Veg Schezwan Burger", category: "Burger", price: 100 },
  { dish_name: "Crispy Paneer Burger", category: "Burger", price: 120 },
  { dish_name: "Crispy Paneer Cheese Burger", category: "Burger", price: 130 },
  { dish_name: "Crispy Paneer Schezwan Burger", category: "Burger", price: 140 },
  { dish_name: "Crispy Paneer Chipotle Burger", category: "Burger", price: 150 },
  { dish_name: "Crispy Paneer + Veg Burger", category: "Burger", price: 200 },
  { dish_name: "Crispy Double Decker Burger", category: "Burger", price: 190 },

  // Momos
  { dish_name: "Veg Steam Momo", category: "Momo", price: 80 },
  { dish_name: "Paneer Steam Momo", category: "Momo", price: 90 },
  { dish_name: "Veg Fried Momo", category: "Momo", price: 90 },
  { dish_name: "Paneer Fried Momo", category: "Momo", price: 100 },
  { dish_name: "Veg Tandoori Momo", category: "Momo", price: 120 },
  { dish_name: "Paneer Tandoori Momo", category: "Momo", price: 130 },
  { dish_name: "Veg Mexican Momo", category: "Momo", price: 150 },
  { dish_name: "Paneer Mexican Momo", category: "Momo", price: 160 },
  { dish_name: "Veg + Paneer Steam Momo", category: "Momo", price: 100 },
  { dish_name: "Veg + Paneer Fried Momo", category: "Momo", price: 120 },

  // Extra
  { dish_name: "Cheese", category: "Extra", price: 30 },


  { dish_name: "Water Bottle", category: "Misc", price: 20 },
  { dish_name: "Cigarettes", category: "Misc", price: 25 }
];

let seedDishes = async () => {
    for (let dish of dishes) {
        let { dish_name, category, price } = dish;
        let query = `INSERT INTO dishes (dish_name, category, price) VALUES ($1, $2, $3)`;
        await pool.query(query, [dish_name, category, price]);
    }   
    console.log("Seeding completed");
    process.exit();
   
};

seedDishes();