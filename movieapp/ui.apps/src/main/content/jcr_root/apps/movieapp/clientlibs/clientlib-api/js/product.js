document.addEventListener("DOMContentLoaded", function () {
  const categorySelect = document.getElementById("categorySelect");
  const productGrid = document.getElementById("productGrid");
  const paginationControls = document.getElementById("paginationControls");

  let currentPage = 1;
  const itemsPerPage = 4; // Number of items to show per page
  let allProducts = []; // To store fetched products

  // Fetch products when the category is selected
  categorySelect.addEventListener("change", function () {
    const selectedCategory = categorySelect.value;
    fetchProducts(selectedCategory);
  });

  // Function to fetch products based on category
  function fetchProducts(category) {
    const url = `http://localhost:4502/bin/myapp/fakestore/products${
      category ? `?category=${category}` : ""
    }`;

    fetch(url)
      .then((response) => {
        if (!response.ok) {
          throw new Error("Network response was not ok");
        }
        return response.json();
      })
      .then((products) => {
        allProducts = products; // Store fetched products
        displayProducts(allProducts, currentPage); // Display first page
        setupPagination(allProducts); // Set up pagination
      })
      .catch((error) => {
        console.error("There was a problem with the fetch operation:", error);
      });
  }

  // Function to display products in the grid
  function displayProducts(products, page) {
    productGrid.innerHTML = ""; // Clear the current products

    const startIndex = (page - 1) * itemsPerPage;
    const endIndex = Math.min(startIndex + itemsPerPage, products.length);
    const productsToDisplay = products.slice(startIndex, endIndex);

    if (productsToDisplay.length === 0) {
      productGrid.innerHTML = "<p>No products found.</p>";
      return;
    }

    productsToDisplay.forEach((product) => {
      const productCard = document.createElement("div");
      productCard.className = "product-card";
      productCard.innerHTML = `
              <img src="${product.image}" alt="${product.title}" class="product-image">
              <h3>${product.title}</h3>
              <p class="category">${product.category}</p>
              <p>$${product.price}</p>
              <button class="cart-button" data-id="${product.id}">Add to Cart</button>
          `;
      productGrid.appendChild(productCard);
    });

    // Attach event listeners to cart buttons
    document.querySelectorAll(".cart-button").forEach((button) => {
      button.addEventListener("click", () => {
        const productId = button.getAttribute("data-id");
        fetchProductDetails(productId); // Fetch and display product details
      });
    });
  }

  // Function to fetch and display product details
  function fetchProductDetails(id) {
    fetch(`http://localhost:4502/bin/myapp/fakestore/products?id=${id}`)
      .then((response) => {
        if (!response.ok) {
          throw new Error("Network response was not ok");
        }
        return response.json();
      })
      .then((product) => displayProductDetails(product))
      .catch((error) =>
        console.error("Error fetching product details:", error)
      );
  }

  // Function to display product details
  function displayProductDetails(product) {
    const detailCard = document.createElement("div");
    detailCard.className = "product-detail-card";

    detailCard.innerHTML = `
      <img src="${product.image}" alt="${product.title}" class="product-image">
      <h2>${product.title}</h2>
      <p class="category">${product.category}</p>
      <p>${product.description}</p>
      <p>Price: $${product.price.toFixed(2)}</p>
      <p>Rating: ${product.rating.rate} (${product.rating.count} reviews)</p>
      <button id="backButton">Back to Products</button>
    `;

    productGrid.innerHTML = ""; // Clear product grid
    productGrid.appendChild(detailCard);

    // Add event listener to the back button
    document.getElementById("backButton").addEventListener("click", () => {
      displayProducts(allProducts, currentPage); // Reload product grid
    });
  }

  // Setup pagination
  function setupPagination(products) {
    paginationControls.innerHTML = ""; // Clear previous pagination

    const totalPages = Math.ceil(products.length / itemsPerPage);

    if (totalPages <= 1) return; // No pagination needed

    const prevButton = document.createElement("button");
    prevButton.innerText = "Previous";
    prevButton.disabled = currentPage === 1;
    prevButton.addEventListener("click", () => {
      if (currentPage > 1) {
        currentPage--;
        displayProducts(products, currentPage);
        setupPagination(products);
      }
    });
    paginationControls.appendChild(prevButton);

    const nextButton = document.createElement("button");
    nextButton.innerText = "Next";
    nextButton.disabled = currentPage === totalPages;
    nextButton.addEventListener("click", () => {
      if (currentPage < totalPages) {
        currentPage++;
        displayProducts(products, currentPage);
        setupPagination(products);
      }
    });
    paginationControls.appendChild(nextButton);
  }
});
