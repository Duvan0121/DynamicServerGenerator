console.log('Bienvenido a la API');

//----INFORMACION GENERAL DE LA API----
//Title: API de Frutas
//Description: Esta API permite gestionar información sobre frutas.
//Version: 1.0.0

//----DATA PARA EL OBJETO fruta----
const frutaList = [{id: 1,nombre: "99ea720d", color: "3c9ff0d5", precio: 41.04021067405878},{id: 2,nombre: "c268c9a0", color: "9a90fe88", precio: 23.83011009039725},{id: 3,nombre: "4dc52fd9", color: "8ac88712", precio: 8.405632736231505},{id: 4,nombre: "c3af6911", color: "0b3d104e", precio: 90.82906018096917},{id: 5,nombre: "a14e78c7", color: "0e13c946", precio: 83.3518832961444}];

//----DATA PARA EL OBJETO carro----
const carroList = [{id: 1,nombrecar: "538d35f0", colorcar: "6e3d9ec4", preciocar: 67.65610710066991},{id: 2,nombrecar: "1669a69b", colorcar: "c54d9862", preciocar: 33.122863627771615},{id: 3,nombrecar: "e88e3de9", colorcar: "0809b83d", preciocar: 3.670705734138935},{id: 4,nombrecar: "e8c34dcf", colorcar: "69678077", preciocar: 62.056767051960605},{id: 5,nombrecar: "7471a3f7", colorcar: "a60e5466", preciocar: 65.72910720486162}];


//----ENDPOINTS DEL OBJETO /fruta----
//Se hace un GET general sobre '/frutas'
app.get('/frutas', (req, res) => {
    res.status(200).json(frutaList);
});

//Se hace un GET individual sobre '/fruta'
app.get('/fruta/:id', (req, res) => {
    const item = frutaList.find(i => i.id === parseInt(req.params.id));
    if (item) res.status(200).json(item);
    else res.status(404).json({ message: 'fruta no encontrado' });
});

//Se hace un POST sobre '/fruta'
app.post('/fruta', (req, res) => {
    delete req.body.id;
    const newItem = { id: frutaList.length + 1, ...req.body };
    if (Object.keys(newItem).length < 4) {
        res.status(400).json('Faltan campos requeridos');
    } else {
        frutaList.push(newItem);
        res.status(201).json(newItem);
    }
});

//Se hace un PUT individual sobre '/fruta'
app.put('/fruta/:id', (req, res) => {
    const itemIndex = frutaList.findIndex(i => i.id === parseInt(req.params.id));
    if (itemIndex !== -1) {
        frutaList[itemIndex] = { id: frutaList[itemIndex].id, ...req.body };
        res.json(frutaList[itemIndex]);
    } else {
        res.status(404).json({ message: 'fruta no encontrado' });
    }
});

//Se hace un DELETE individual sobre '/frutas'
app.delete('/fruta/:id', (req, res) => {
    const itemIndex = frutaList.findIndex(i => i.id === parseInt(req.params.id));
    if (itemIndex !== -1) {
        frutaList.splice(itemIndex, 1);
        res.status(200).json({message: 'fruta Eliminado'});
    } else {
        res.status(404).json({ message: 'fruta no encontrado' });
    }
});


//----ENDPOINTS DEL OBJETO /carro----
//Se hace un GET general sobre '/carros'
app.get('/carros', (req, res) => {
    res.status(200).json(carroList);
});

//Se hace un GET individual sobre '/carro'
app.get('/carro/:id', (req, res) => {
    const item = carroList.find(i => i.id === parseInt(req.params.id));
    if (item) res.status(200).json(item);
    else res.status(404).json({ message: 'carro no encontrado' });
});

//Se hace un POST sobre '/carro'
app.post('/carro', (req, res) => {
    delete req.body.id;
    const newItem = { id: carroList.length + 1, ...req.body };
    if (Object.keys(newItem).length < 4) {
        res.status(400).json('Faltan campos requeridos');
    } else {
        carroList.push(newItem);
        res.status(201).json(newItem);
    }
});

//Se hace un PUT individual sobre '/carro'
app.put('/carro/:id', (req, res) => {
    const itemIndex = carroList.findIndex(i => i.id === parseInt(req.params.id));
    if (itemIndex !== -1) {
        carroList[itemIndex] = { id: carroList[itemIndex].id, ...req.body };
        res.json(carroList[itemIndex]);
    } else {
        res.status(404).json({ message: 'carro no encontrado' });
    }
});

//Se hace un DELETE individual sobre '/carros'
app.delete('/carro/:id', (req, res) => {
    const itemIndex = carroList.findIndex(i => i.id === parseInt(req.params.id));
    if (itemIndex !== -1) {
        carroList.splice(itemIndex, 1);
        res.status(200).json({message: 'carro Eliminado'});
    } else {
        res.status(404).json({ message: 'carro no encontrado' });
    }
});

