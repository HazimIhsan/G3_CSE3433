const express = require('express');
const cors = require('cors');
const app = express();

// Set this microservice to run on Port 5002!
const PORT = 5002;

app.use(cors());
app.use(express.json());

// In-memory inventory tracking database for your prototype
let inventoryDb = {
    water: 500,   // Starting with 500 boxes of water
    medical: 100, // Starting with 100 first aid kits
    tents: 50     // Starting with 50 emergency tents
};

// ENDPOINT 1: Allows other components to see what items are currently in stock
app.get('/api/resources', (req, res) => {
    res.json(inventoryDb);
});

// ENDPOINT 2: Allows our future coordinator to deduct items when they get dispatched
app.post('/api/resources/deduct', (req, res) => {
    const item = req.body.item;       // e.g., "water"
    const quantity = parseInt(req.body.quantity); // e.g., 50

    if (inventoryDb[item] !== undefined) {
        // Subtract the requested amount from our warehouse supply stockpile
        inventoryDb[item] -= quantity;
        console.log(`📦 INVENTORY UPDATE: Deducted ${quantity} from ${item}. Remaining stock: ${inventoryDb[item]}`);
        
        res.json({ status: "Success", remaining: inventoryDb[item] });
    } else {
        res.status(400).json({ status: "Error", message: "Item not found in warehouse." });
    }
});

app.listen(PORT, () => {
    console.log(`📦 Resource Microservice is running on http://localhost:${PORT}`);
});