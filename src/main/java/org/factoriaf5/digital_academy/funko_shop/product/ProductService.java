package org.factoriaf5.digital_academy.funko_shop.product;

import org.factoriaf5.digital_academy.funko_shop.category.Category;
import org.factoriaf5.digital_academy.funko_shop.category.CategoryDTO;
import org.factoriaf5.digital_academy.funko_shop.category.CategoryRepository;
import org.factoriaf5.digital_academy.funko_shop.category.category_exceptions.CategoryNotFoundException;
import org.factoriaf5.digital_academy.funko_shop.product.product_exceptions.ProductNotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public ProductDTO createProduct(ProductDTO productDto, Long categoryId) {
        Category category = getCategoryById(categoryId);

        Product product = mapToEntity(productDto);
        product.setCategory(category);

        Product savedProduct = productRepository.save(product);
        return mapToDTO(savedProduct);
    }

    public Page<ProductDTO> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(this::mapToDTO);
    }

    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        return mapToDTO(product);
    }

    public Page<ProductDTO> getProductsByCategory(Long categoryId, Pageable pageable) {
        Category category = getCategoryById(categoryId);
        return productRepository.findByCategory(category, pageable)
                .map(this::mapToDTO);
    }

    public Page<ProductDTO> searchProductsByKeyword(String keyword, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCase(keyword, pageable)
                .map(this::mapToDTO);
    }

    public ProductDTO updateProduct(Long id, ProductDTO productDto) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        updateProductFields(existingProduct, productDto);

        Product updatedProduct = productRepository.save(existingProduct);
        return mapToDTO(updatedProduct);
    }

    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    private Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + categoryId));
    }

    public List<ProductDTO> getDiscountedProducts() {
        List<Product> products = productRepository.findByDiscount();
        return products.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private void updateProductFields(Product product, ProductDTO productDto) {
        product.setName(productDto.getName());
        product.setImageHash(productDto.getImageHash());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setStock(productDto.getStock());
        product.setAvailable(productDto.isAvailable());
        product.setDiscount(productDto.getDiscount());

        if (productDto.getCategory() != null) {
            product.setCategory(getCategoryById(productDto.getCategory().getId()));
        }
    }

    public List<ProductDTO> getNewProducts() {
        List<Product> newProducts = productRepository.findByIsNewTrue();

        return newProducts.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private Product mapToEntity(ProductDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setImageHash(dto.getImageHash());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setAvailable(dto.isAvailable());
        product.setNew(dto.isNew());
        product.setDiscount(dto.getDiscount());

        return product;
    }

    private ProductDTO mapToDTO(Product product) {
        CategoryDTO categoryDTO = null;
        if (product.getCategory() != null) {
            categoryDTO = new CategoryDTO(
                    product.getCategory().getId(),
                    product.getCategory().getName(),
                    product.getCategory().getImageHash()
            );
        }

        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getImageHash(),
                product.getDescription(),
                product.getPrice(),
                product.getDiscountedPrice(),
                product.getStock(),
                product.isAvailable(),
                product.isNew(),
                categoryDTO,
                product.getDiscount()  
        );
    }

}
