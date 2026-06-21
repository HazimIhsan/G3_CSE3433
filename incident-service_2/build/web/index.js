/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */


import { MongoClient } from 'mongodb';
async function runGetStarted() {
  // Replace the uri string with your connection string
  const uri = 'mongodb+srv://ihsan_db_user:admin123@cluster0.i8foohl.mongodb.net/?appName=Cluster0';
  const client = new MongoClient(uri);
  try {
    const database = client.db('sample_mflix');
    const movies = database.collection('movies');
    // Queries for a movie that has a title value of 'Back to the Future'
    const query = { title: 'Back to the Future' };
    const movie = await movies.findOne(query);
    console.log(movie);
  } finally {
    await client.close();
  }
}
runGetStarted().catch(console.dir);