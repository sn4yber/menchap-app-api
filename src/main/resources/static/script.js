document.addEventListener("DOMContentLoaded", () => {
    let editandoId = null;

    const loginForm = document.getElementById("loginForm");
    const usernameInput = document.getElementById("username");
    const passwordInput = document.getElementById("password");

    const dashboard = document.getElementById("dashboard");
    const loginScreen = document.getElementById("loginScreen");

    const nombreInput = document.getElementById("productName");
    const tipoInput = document.getElementById("productType");
    const cantidadInput = document.getElementById("productQuantity");
    const precioInput = document.getElementById("productPrice");
    const btnGuardar = document.getElementById("saveProductBtn");
    const cuerpoTabla = document.getElementById("inventoryBody");

    loginForm.addEventListener("submit", async (e) => {
        e.preventDefault();

        const usuario = usernameInput.value;
        const contrasena = passwordInput.value;

        const response = await fetch("/api/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ usuario, contrasena })
        });

        if (response.ok) {
            loginScreen.style.display = "none";
            dashboard.style.display = "block";
            cargarInventario();
        } else {
            document.getElementById("loginErrorMessage").textContent = "Credenciales inválidas";
            document.getElementById("loginAlert").style.display = "block";
        }
    });

    // Mostrar modal agregar producto
    window.openAddModal = () => {
        document.getElementById("modalTitle").textContent = "Nuevo Producto";
        document.getElementById("productForm").reset();
        document.getElementById("productModal").style.display = "block";
        editandoId = null;
    };

    // Cerrar modal producto
    window.closeProductModal = () => {
        document.getElementById("productModal").style.display = "none";
    };

    // Guardar producto
    btnGuardar.addEventListener("click", async (e) => {
        e.preventDefault();

        const producto = {
            nombre: nombreInput.value,
            tipo: tipoInput.value,
            cantidad: parseInt(cantidadInput.value),
            precio: parseFloat(precioInput.value)
        };

        const url = editandoId ? `/api/inventario/${editandoId}` : "/api/inventario";
        const method = editandoId ? "PUT" : "POST";

        const response = await fetch(url, {
            method,
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(producto)
        });

        if (response.ok) {
            cargarInventario();
            closeProductModal();
        } else {
            alert("Error al guardar el producto");
        }
    });

    // Cargar inventario
    async function cargarInventario() {
        const response = await fetch("/api/inventario");
        const productos = await response.json();

        cuerpoTabla.innerHTML = "";

        if (productos.length === 0) {
            document.getElementById("emptyState").style.display = "block";
            return;
        }

        document.getElementById("emptyState").style.display = "none";

        productos.forEach(p => {
            const fila = document.createElement("tr");
            fila.innerHTML = `
                <td><input type="checkbox" class="select-product" data-id="${p.id}"></td>
                <td>${p.id}</td>
                <td>${p.nombre}</td>
                <td>${p.tipo}</td>
                <td>${p.cantidad}</td>
                <td>${p.precio}</td>
                <td>${(p.precio * p.cantidad).toFixed(2)}</td>
                <td>
                    <button onclick="editarProducto(${p.id}, '${p.nombre}', '${p.tipo}', ${p.cantidad}, ${p.precio})">Editar</button>
                    <button onclick="eliminarProducto(${p.id})">Eliminar</button>
                </td>
            `;
            cuerpoTabla.appendChild(fila);
        });
    }

    // Editar producto
    window.editarProducto = (id, nombre, tipo, cantidad, precio) => {
        editandoId = id;
        nombreInput.value = nombre;
        tipoInput.value = tipo;
        cantidadInput.value = cantidad;
        precioInput.value = precio;

        document.getElementById("modalTitle").textContent = "Editar Producto";
        document.getElementById("productModal").style.display = "block";
    };

    // Eliminar producto
    window.eliminarProducto = async (id) => {
        const confirmar = confirm("¿Deseas eliminar este producto?");
        if (confirmar) {
            const response = await fetch(`/api/inventario/${id}`, { method: "DELETE" });
            if (response.ok) cargarInventario();
            else alert("Error al eliminar producto");
        }
    };

    // Editar desde botón principal
    window.editSelected = () => {
        const selected = document.querySelector(".select-product:checked");
        if (!selected) {
            alert("Selecciona un producto para editar");
            return;
        }
        const fila = selected.closest("tr");
        const id = parseInt(selected.dataset.id);
        const nombre = fila.children[2].textContent;
        const tipo = fila.children[3].textContent;
        const cantidad = parseInt(fila.children[4].textContent);
        const precio = parseFloat(fila.children[5].textContent);

        editarProducto(id, nombre, tipo, cantidad, precio);
    };

    // Eliminar desde botón principal
    window.deleteSelected = () => {
        const selected = document.querySelector(".select-product:checked");
        if (!selected) {
            alert("Selecciona un producto para eliminar");
            return;
        }
        const id = selected.dataset.id;
        eliminarProducto(id);
    };

    window.refreshInventory = () => cargarInventario();

    window.toggleSelectAll = () => {
        const checkboxes = document.querySelectorAll(".select-product");
        const selectAll = document.getElementById("selectAll").checked;
        checkboxes.forEach(chk => chk.checked = selectAll);
    };

    // Mostrar nombre de usuario en pantalla (opcional)
    usernameInput.addEventListener("input", () => {
        document.getElementById("userDisplay").textContent = `Usuario: ${usernameInput.value}`;
    });
});
