const chai = require('chai');
const chaiHttp = require('chai-http');
const server = require('../src/app');

chai.use(chaiHttp);
chai.should();

let user_id = '';

// Test the GET route for '/'
describe('GET /', () => {

    // It should return '<h1>Welcome to the API.</h1>'
    it('should return welcome message', (done) => {
        chai.request(server)
            .get('/')
            .end((err, res) => {
                res.should.have.status(200);
                res.text.should.be.eql('<h1>Welcome to the API.</h1>');
                done();
            });
    });
});

// Test the POST route for '/api/register'
describe('POST /api/register with invalid email', () => {

    // In the body:
    // {
        // "name": "John Doe",
        // "email": "thisisanemail",
        // "password": "thisisapassword"
    // }
    // It should return a status code of 400
    it('should return a status code of 400', (done) => {
        chai.request(server)
            .post('/api/register')
            .send({
                name: 'John Doe',
                email: 'thisisanemail',
                password: 'thisisapassword'
            })
            .end((err, res) => {
                res.should.have.status(400);
                done();
            });
    });
});

// Test the POST route for '/api/register'
describe('POST /api/register without email', () => {

    // In the body:
    // {
        // "name": "John Doe",
        // "password": "thisisapassword"
    // }
    // It should return a status code of 400
    it('should return a status code of 400', (done) => {
        chai.request(server)
            .post('/api/register')
            .send({
                name: 'John Doe',
                password: 'thisisapassword'
            })
            .end((err, res) => {
                // print the response body
                res.should.have.status(400);
                done();
            });
    });
});

// Test the POST route for '/api/register'
describe('POST /api/register with complete and valid data', () => {

    // In the body:
    // {
        // "name": "John Doe",
        // "email": "johndoe@gmail.com",
        // "password": "123456"
    // }
    // It should return a status code of 201
    it('should return a status code of 201', (done) => {
        chai.request(server)
            .post('/api/register')
            .send({
                name: 'John Doe',
                email: 'johndoe@gmail.com',
                password: '12345678'
            })
            .end((err, res) => {
                res.should.have.status(201);
                done();
            });
    });
});

// Test the POST route for '/api/login'
describe('POST /api/login', () => {
    
        // In the body:
        // {
            // "email": "johndoe@gmail.com",
            // "password": "12345678"
        // }
        // It should return a status code of 200
        it('Logging in a user...', (done) => {
            chai.request(server)
                .post('/api/login')
                .send({
                    email: 'johndoe@gmail.com',
                    password: '12345678'
                })
                .end((err, res) => {
                    res.should.have.status(200);
                    // Store user_id in a variable
                    user_id = res.body.user_id;
                    done();
                });
        });
});

// Test the GET route for '/api/user/:user_id'
describe('GET /api/user/:user_id with nonexistent id', () => {
    // It should return a status code of 404
    it('should return a status code of 404', (done) => {
        chai.request(server)
            .get('/api/user/' + '123')
            .end((err, res) => {
                res.should.have.status(404);
                done();
            });
    });
});

// Test the GET route for '/api/user/:user_id'
describe('GET /api/user/:user_id', () => {
    // user_id is from the previous test
    // It should return a status code of 200
    it('should return a status code of 200', (done) => {
        chai.request(server)
            .get('/api/user/' + user_id)
            .end((err, res) => {
                res.should.have.status(200);
                done();
            });
    });
});

// Test the GET route for '/api/user_details/:user_id'
describe('GET /api/user_details/:user_id with nonexistent id', () => {
    // It should return a status code of 404
    it('should return a status code of 404', (done) => {
        chai.request(server)
            .get('/api/user_details/' + '123')
            .end((err, res) => {
                res.should.have.status(404);
                done();
            });
    });
});

// Test the GET route for '/api/user_details/:user_id'
describe('GET /api/user_details/:user_id', () => {
    // user_id is from the previous test
    // It should return a status code of 200
    it('should return a status code of 200', (done) => {
        chai.request(server)
            .get('/api/user_details/' + user_id)
            .end((err, res) => {
                res.should.have.status(200);
                done();
            });
    });
});

// Test the PUT route for '/api/user/:user_id'
describe('PUT /api/user/:user_id with nonexistent id', () => {
    // It should return a status code of 404
    it('should return a status code of 404', (done) => {
        chai.request(server)
            .put('/api/user/' + '123')
            .end((err, res) => {
                res.should.have.status(404);
                done();
            });
    });
});

// Test the PUT route for '/api/user/:user_id'
describe('PUT /api/user/:user_id', () => {
    // user_id is from the previous test
    // It should return a status code of 204
    it('should return a status code of 204', (done) => {
        chai.request(server)
            .put('/api/user/' + user_id)
            .send({
                name: 'Doe John',
                email: 'johndoe@yahoo.com'
            })
            .end((err, res) => {
                res.should.have.status(204);
                done();
            });
    });
});

// Test the PUT route for '/api/user_details/:user_id'
describe('PUT /api/user_details/:user_id with nonexistent id', () => {
    // It should return a status code of 404
    it('should return a status code of 404', (done) => {
        chai.request(server)
            .put('/api/user_details/' + '123')
            .end((err, res) => {
                res.should.have.status(404);
                done();
            });
    });
});

// Test the PUT route for '/api/user_details/:user_id'
describe('PUT /api/user_details/:user_id', () => {
    // user_id is from the previous test
    // It should return a status code of 204
    it('should return a status code of 204', (done) => {
        chai.request(server)
            .put('/api/user_details/' + user_id)
            .send({
                address: '123 Main St',
                phone: '123-456-7890'
            })
            .end((err, res) => {
                res.should.have.status(204);
                done();
            });
    });
});

// Test the POST route for '/api/detection_history/:user_id'
describe('POST /api/detection_history with nonexistent id', () => {
    // It should return a status code of 404
    it('should return a status code of 404', (done) => {
        chai.request(server)
            .post('/api/detection_history/' + '123')
            .end((err, res) => {
                res.should.have.status(404);
                done();
            });
    });
});

// Test the POST route for '/api/detection_history/:user_id'
describe('POST /api/detection_history', () => {
    // user_id is from the previous test
    // It should return a status code of 201
    it('should return a status code of 201', (done) => {
        chai.request(server)
            .post('/api/detection_history/')
            .send({
                user_id: user_id,
                condition_title: 'Fever',
                recommendation: 'Take an aspirin'
            })
            .end((err, res) => {
                res.should.have.status(201);
                done();
            });
    });
});

// Take the GET route for '/api/detection_history/:user_id'
describe('GET /api/detection_history/:user_id with nonexistent id', () => {
    // It should return a status code of 404
    it('should return a status code of 404', (done) => {
        chai.request(server)
            .get('/api/detection_history/' + '123')
            .end((err, res) => {
                res.should.have.status(404);
                done();
            });
    });
});

// Test the GET route for '/api/detection_history/:user_id'
describe('GET /api/detection_history/:user_id', () => {
    // user_id is from the previous test
    // It should return a status code of 200
    it('should return a status code of 200', (done) => {
        chai.request(server)
            .get('/api/detection_history/' + user_id)
            .end((err, res) => {
                res.should.have.status(200);
                // Count the detection_history part of the response
                res.body.detection_history.length.should.be.above(0);
                done();
            });
    });
});

// TESTS CLEANUP
// Use .env to connect to the database and delete the user
const pgp = require('pg-promise')();
require('dotenv').config();
const db = pgp(process.env.DATABASE_URL);

// Delete the user with the db connection: email 'johndoe@gmail.com'
describe('Deleting all tests data...', () => {
    it('Deleting all tests data...', (done) => {
        db.none('DELETE FROM public.user WHERE user_id = $1', user_id)
            .then(() => {
                // Close the db connection
                pgp.end();
                done();
            });
    });
});