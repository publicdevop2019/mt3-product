package com.hw.aggregate.product.model;

import com.hw.aggregate.product.ProductApplicationService;
import com.hw.aggregate.product.ProductDetailRepo;
import com.hw.aggregate.product.command.CreateProductAdminCommand;
import com.hw.aggregate.product.command.ProductValidationCommand;
import com.hw.aggregate.product.command.UpdateProductAdminCommand;
import com.hw.aggregate.product.exception.*;
import com.hw.shared.Auditable;
import com.hw.shared.PatchCommand;
import com.hw.shared.StringSetConverter;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.hw.aggregate.product.representation.ProductDetailAdminRep.*;
import static com.hw.aggregate.product.representation.ProductDetailAdminRep.ProductSkuAdminRepresentation.*;
import static com.hw.shared.AppConstant.*;


@Data
@Entity
@Table(name = "biz_product")
@NoArgsConstructor
@Slf4j
@EntityListeners(MyListener.class)
public class Product extends Auditable {
    @Id
    private Long id;
    public transient static final String ID_LITERAL = "id";

    private String imageUrlSmall;

    private String name;
    public transient static final String NAME_LITERAL = "name";

    private String description;

    private Long endAt;
    public transient static final String END_AT_LITERAL = "endAt";

    private Long startAt;
    public transient static final String START_AT_LITERAL = "startAt";

    @Column(length = 10000)
    @Convert(converter = ProductOptionConverter.class)
    private List<ProductOption> selectedOptions;
    public transient static final String SELECTED_OPTIONS_LITERAL = "selectedOptions";

    @Convert(converter = StringSetConverter.class)
    private Set<String> imageUrlLarge;
    public transient static final String IMAGE_URL_LARGE_LITERAL = "imageUrlLarge";

    @Convert(converter = StringSetConverter.class)
    private Set<String> specification;
    public transient static final String SPEC_LITERAL = "specification";

    @Convert(converter = StringSetConverter.class)
    private Set<String> attrKey;
    public transient static final String ATTR_KEY_LITERAL = "attrKey";

    @Convert(converter = StringSetConverter.class)
    private Set<String> attrProd;
    public transient static final String ATTR_PROD_LITERAL = "attrProd";

    @Convert(converter = StringSetConverter.class)
    private Set<String> attrGen;
    public transient static final String ATTR_GEN_LITERAL = "attrGen";

    @Convert(converter = StringSetConverter.class)
    private Set<String> attrSalesTotal;
    public transient static final String ATTR_SALES_TOTAL_LITERAL = "attrSalesTotal";

    @OneToMany(targetEntity = ProductSku.class, mappedBy = "productId", cascade = {CascadeType.ALL})
    private List<ProductSku> productSkuList;

    @Column(length = 10000)
    private ArrayList<ProductAttrSaleImages> attributeSaleImages;

    @Column(updatable = false)
    private Integer storageOrder;
    public transient static final String STORAGE_ORDER_LITERAL = "storageOrder";

    @Column(updatable = false)
    private Integer storageActual;
    public transient static final String STORAGE_ACTUAL_LITERAL = "storageActual";

    private BigDecimal lowestPrice;
    public transient static final String LOWEST_PRICE_LITERAL = "lowestPrice";

    @Column(updatable = false)
    private Integer totalSales;
    public transient static final String TOTAL_SALES_LITERAL = "totalSales";

    public static Product create(Long id, CreateProductAdminCommand command, ProductDetailRepo repo) {
        Product productDetail = new Product(id, command);
        return repo.save(productDetail);
    }

    public static boolean isAvailable(Product productDetail) {
        Long current = new Date().getTime();
        if (productDetail.getStartAt() == null)
            return false;
        if (current.compareTo(productDetail.getStartAt()) < 0) {
            return false;
        } else {
            if (productDetail.getEndAt() == null) {
                return true;
            } else if (current.compareTo(productDetail.getEndAt()) < 0) {
                return true;
            } else {
                return false;
            }
        }
    }

    public static boolean validate(List<ProductValidationCommand> commands, ProductDetailRepo repo) {
        return commands.stream().anyMatch(command -> {
            Optional<Product> byId = repo.findById(Long.parseLong(command.getProductId()));
            //validate product match
            if (byId.isEmpty() || !Product.isAvailable(byId.get()))
                return true;
            BigDecimal price;
            if (byId.get().getProductSkuList() != null && byId.get().getProductSkuList().size() != 0) {
                List<ProductSku> collect = byId.get().getProductSkuList().stream().filter(productSku -> new TreeSet(productSku.getAttributesSales()).equals(new TreeSet(command.getAttributesSales()))).collect(Collectors.toList());
                price = collect.get(0).getPrice();
            } else {
                price = byId.get().getLowestPrice();
            }
            //if no option present then compare final price
            if (command.getSelectedOptions() == null || command.getSelectedOptions().size() == 0) {
                return price.compareTo(command.getFinalPrice()) != 0;
            }
            //validate product option match
            List<ProductOption> storedOption = byId.get().getSelectedOptions();
            if (storedOption == null || storedOption.size() == 0)
                return true;
            boolean optionAllMatch = command.getSelectedOptions().stream().allMatch(userSelected -> {
                //check selected option is valid option
                Optional<ProductOption> first = storedOption.stream().filter(storedOptionItem -> {
                    // compare title
                    if (!storedOptionItem.title.equals(userSelected.title))
                        return false;
                    //compare option value for each title
                    String optionValue = userSelected.getOptions().get(0).getOptionValue();
                    Optional<OptionItem> first1 = storedOptionItem.options.stream().filter(optionItem -> optionItem.getOptionValue().equals(optionValue)).findFirst();
                    if (first1.isEmpty())
                        return false;
                    return true;
                }).findFirst();
                if (first.isEmpty())
                    return false;
                else {
                    return true;
                }
            });
            if (!optionAllMatch)
                return true;
            //validate product final price
            BigDecimal finalPrice = command.getFinalPrice();
            // get all price variable
            List<String> userSelectedAddOnTitles = command.getSelectedOptions().stream().map(ProductOption::getTitle).collect(Collectors.toList());
            // filter option based on title
            Stream<ProductOption> storedAddonMatchingUserSelection = byId.get().getSelectedOptions().stream().filter(var1 -> userSelectedAddOnTitles.contains(var1.getTitle()));
            // map to value detail for each title
            List<String> priceVarCollection = storedAddonMatchingUserSelection.map(storedMatchAddon -> {
                String title = storedMatchAddon.getTitle();
                //find right option for title
                Optional<ProductOption> user_addon_option = command.getSelectedOptions().stream().filter(e -> e.getTitle().equals(title)).findFirst();
                OptionItem user_optionItem = user_addon_option.get().getOptions().get(0);
                Optional<OptionItem> first = storedMatchAddon.getOptions().stream().filter(db_optionItem -> db_optionItem.getOptionValue().equals(user_optionItem.getOptionValue())).findFirst();
                return first.get().getPriceVar();
            }).collect(Collectors.toList());
            BigDecimal calc = new BigDecimal(0);
            for (String priceVar : priceVarCollection) {
                if (priceVar.contains("+")) {
                    double v = Double.parseDouble(priceVar.replace("+", ""));
                    BigDecimal bigDecimal = BigDecimal.valueOf(v);
                    calc = calc.add(bigDecimal);
                } else if (priceVar.contains("-")) {
                    double v = Double.parseDouble(priceVar.replace("-", ""));
                    BigDecimal bigDecimal = BigDecimal.valueOf(v);
                    calc = calc.subtract(bigDecimal);

                } else if (priceVar.contains("*")) {
                    double v = Double.parseDouble(priceVar.replace("*", ""));
                    BigDecimal bigDecimal = BigDecimal.valueOf(v);
                    calc = calc.multiply(bigDecimal);
                } else {
                    log.error("unknown operation type");
                }
            }
            if (calc.add(price).compareTo(finalPrice) == 0) {
                log.error("value does match for product {}, expected {} actual {}", command.getProductId(), calc.add(price), finalPrice);
                return false;
            }
            return true;
        });
    }

    public void replace(UpdateProductAdminCommand command, ProductApplicationService productApplicationService, ProductDetailRepo repo) {
        this.imageUrlSmall = command.getImageUrlSmall();
        this.name = command.getName();
        this.description = command.getDescription();
        this.selectedOptions = command.getSelectedOptions();
        this.imageUrlLarge = command.getImageUrlLarge();
        this.specification = command.getSpecification();
        this.attrKey = command.getAttributesKey();
        this.attrProd = command.getAttributesProd();
        this.attrGen = command.getAttributesGen();
        this.startAt = command.getStartAt();
        this.endAt = command.getEndAt();
        if (command.getSkus() != null && command.getSkus().size() != 0) {
            command.getSkus().forEach(e -> {
                if (e.getSales() == null)
                    e.setSales(0);
                e.setAttributesSales(new TreeSet<>(e.getAttributesSales()));
            });
            adjustSku(command.getSkus(), productApplicationService);
            this.attrSalesTotal = command.getSkus().stream().map(UpdateProductAdminCommand.UpdateProductAdminSkuCommand::getAttributesSales).flatMap(Collection::stream).collect(Collectors.toSet());
            this.attributeSaleImages = command.getAttributeSaleImages().stream().map(e ->
                    {
                        ProductAttrSaleImages productAttrSaleImages = new ProductAttrSaleImages();
                        productAttrSaleImages.setAttributeSales(e.getAttributeSales());
                        productAttrSaleImages.setImageUrls((LinkedHashSet<String>) e.getImageUrls());
                        return productAttrSaleImages;
                    }
            ).collect(Collectors.toCollection(ArrayList::new));
            this.lowestPrice = findLowestPrice(this);
        } else {
            this.productSkuList = null;
            this.lowestPrice = command.getPrice();
            ArrayList<PatchCommand> patchCommands = new ArrayList<>();
            if (command.getDecreaseOrderStorage() != null) {
                PatchCommand patchCommand = new PatchCommand();
                patchCommand.setOp(PATCH_OP_TYPE_DIFF);
                String query = toNoSkuQueryPath(command, this);
                patchCommand.setPath(query);
                patchCommand.setValue(command.getDecreaseOrderStorage());
                patchCommands.add(patchCommand);
            }
            if (command.getDecreaseActualStorage() != null) {
                PatchCommand patchCommand = new PatchCommand();
                patchCommand.setOp(PATCH_OP_TYPE_DIFF);
                String query = toNoSkuQueryPath(command, this);
                patchCommand.setPath(query);
                patchCommand.setValue(command.getDecreaseActualStorage());
                patchCommands.add(patchCommand);
            }
            if (command.getIncreaseOrderStorage() != null) {
                PatchCommand patchCommand = new PatchCommand();
                patchCommand.setOp(PATCH_OP_TYPE_SUM);
                String query = toNoSkuQueryPath(command, this);
                patchCommand.setPath(query);
                patchCommand.setValue(command.getIncreaseOrderStorage());
                patchCommands.add(patchCommand);
            }
            if (command.getIncreaseActualStorage() != null) {
                PatchCommand patchCommand = new PatchCommand();
                patchCommand.setOp(PATCH_OP_TYPE_SUM);
                String query = toNoSkuQueryPath(command, this);
                patchCommand.setPath(query);
                patchCommand.setValue(command.getIncreaseActualStorage());
                patchCommands.add(patchCommand);
            }
            String changeId = UUID.randomUUID().toString();
            productApplicationService.patchForAdmin(patchCommands, changeId);
        }
        repo.save(this);
    }

    private String toNoSkuQueryPath(UpdateProductAdminCommand command, Product productDetail) {
        if (command.getDecreaseOrderStorage() != null || command.getIncreaseOrderStorage() != null) {
            return "/" + productDetail.getId() + "/" + ADMIN_REP_STORAGE_ORDER_LITERAL;
        }
        if (command.getDecreaseActualStorage() != null || command.getIncreaseActualStorage() != null) {
            return "/" + productDetail.getId() + "/" + ADMIN_REP_STORAGE_ACTUAL_LITERAL;
        }
        return null;
    }

    private void adjustSku(List<UpdateProductAdminCommand.UpdateProductAdminSkuCommand> commands, ProductApplicationService productApplicationService) {
        commands.forEach(command -> {
            if (command.getStorageActual() != null && command.getStorageOrder() != null) {
                // new sku
                boolean b = this.productSkuList.stream().anyMatch(e -> e.getAttributesSales().equals(command.getAttributesSales()));
                if (b)
                    throw new SkuAlreadyExistException();
                ProductSku productSku = new ProductSku();
                productSku.setSales(command.getSales() == null ? 0 : command.getSales());
                productSku.setStorageActual(command.getStorageActual());
                productSku.setStorageOrder(command.getStorageOrder());
                productSku.setAttributesSales(command.getAttributesSales());
                productSku.setPrice(command.getPrice());
                this.productSkuList.add(productSku);
            } else {
                //existing sku
                Optional<ProductSku> first = this.productSkuList.stream().filter(e -> e.getAttributesSales().equals(command.getAttributesSales())).findFirst();
                if (first.isEmpty())
                    throw new SkuNotExistException();
                //update price
                ProductSku productSku = first.get();
                productSku.setPrice(command.getPrice());
                updateStorage(productApplicationService, command);

            }
        });
        // find skus not in update command & remove
        List<ProductSku> collect = this.productSkuList.stream().filter(e -> commands.stream().noneMatch(command -> command.getAttributesSales().equals(e.getAttributesSales()))).collect(Collectors.toList());
        this.productSkuList.removeAll(collect);
    }

    private void updateStorage(ProductApplicationService productApplicationService, UpdateProductAdminCommand.UpdateProductAdminSkuCommand command) {
        ArrayList<PatchCommand> patchCommands = new ArrayList<>();
        if (command.getDecreaseOrderStorage() != null) {
            PatchCommand patchCommand = new PatchCommand();
            patchCommand.setOp(PATCH_OP_TYPE_DIFF);
            String query = toSkuQueryPath(command, this);
            patchCommand.setPath(query);
            patchCommand.setValue(command.getDecreaseOrderStorage());
            patchCommands.add(patchCommand);
        }
        if (command.getDecreaseActualStorage() != null) {
            PatchCommand patchCommand = new PatchCommand();
            patchCommand.setOp(PATCH_OP_TYPE_DIFF);
            String query = toSkuQueryPath(command, this);
            patchCommand.setPath(query);
            patchCommand.setValue(command.getDecreaseActualStorage());
            patchCommands.add(patchCommand);
        }
        if (command.getIncreaseOrderStorage() != null) {
            PatchCommand patchCommand = new PatchCommand();
            patchCommand.setOp(PATCH_OP_TYPE_SUM);
            String query = toSkuQueryPath(command, this);
            patchCommand.setPath(query);
            patchCommand.setValue(command.getIncreaseOrderStorage());
            patchCommands.add(patchCommand);
        }
        if (command.getIncreaseActualStorage() != null) {
            PatchCommand patchCommand = new PatchCommand();
            patchCommand.setOp(PATCH_OP_TYPE_SUM);
            String query = toSkuQueryPath(command, this);
            patchCommand.setPath(query);
            patchCommand.setValue(command.getIncreaseActualStorage());
            patchCommands.add(patchCommand);
        }
        String changeId = UUID.randomUUID().toString();
        productApplicationService.patchForAdmin(patchCommands, changeId);
    }

    private String toSkuQueryPath(UpdateProductAdminCommand.UpdateProductAdminSkuCommand command, Product productDetail) {
        Set<String> attributesSales1 = command.getAttributesSales();
        String join = String.join(",", attributesSales1);
        String replace = join.replace(":", "-").replace("/", "~/");

        String s = "/" + productDetail.getId() + "/" + ADMIN_REP_SKU_LITERAL + "?" + HTTP_PARAM_QUERY + "=" + ADMIN_REP_ATTR_SALES_LITERAL + ":" + replace;
        if (command.getDecreaseOrderStorage() != null || command.getIncreaseOrderStorage() != null) {
            return s + "/" + ADMIN_REP_SKU_STORAGE_ORDER_LITERAL;
        }
        if (command.getDecreaseActualStorage() != null || command.getIncreaseActualStorage() != null) {
            return s + "/" + ADMIN_REP_SKU_STORAGE_ACTUAL_LITERAL;
        }
        return null;
    }


    private Product(Long id, CreateProductAdminCommand command) {
        this.id = id;
        this.imageUrlSmall = command.getImageUrlSmall();
        this.name = command.getName();
        this.description = command.getDescription();
        this.selectedOptions = command.getSelectedOptions();
        this.imageUrlLarge = command.getImageUrlLarge();
        this.specification = command.getSpecification();
        this.attrKey = command.getAttributesKey();
        this.attrProd = command.getAttributesProd();
        this.attrGen = command.getAttributesGen();
        this.startAt = (command.getStartAt());
        this.endAt = (command.getEndAt());
        if (command.getSkus() != null && command.getSkus().size() != 0) {
            command.getSkus().forEach(e -> {
                if (e.getSales() == null)
                    e.setSales(0);
                e.setAttributesSales(e.getAttributesSales());
            });
            this.attrSalesTotal = command.getSkus().stream().map(CreateProductAdminCommand.CreateProductSkuAdminCommand::getAttributesSales).flatMap(Collection::stream).collect(Collectors.toSet());
            this.productSkuList = command.getSkus().stream().map(e -> {
                ProductSku productSku = new ProductSku();
                productSku.setPrice(e.getPrice());
                productSku.setProductId(this.id);
                productSku.setAttributesSales(e.getAttributesSales());
                productSku.setStorageOrder(e.getStorageOrder());
                productSku.setStorageActual(e.getStorageActual());
                productSku.setSales(e.getSales());
                return productSku;
            }).collect(Collectors.toList());
            this.attributeSaleImages = command.getAttributeSaleImages().stream().map(e ->
                    {
                        ProductAttrSaleImages productAttrSaleImages = new ProductAttrSaleImages();
                        productAttrSaleImages.setAttributeSales(e.getAttributeSales());
                        productAttrSaleImages.setImageUrls((LinkedHashSet<String>) e.getImageUrls());
                        return productAttrSaleImages;
                    }
            ).collect(Collectors.toCollection(ArrayList::new));
            this.lowestPrice = findLowestPrice(this);
            this.totalSales = calcTotalSales(this);
        } else {
            this.storageOrder = command.getStorageOrder();
            this.storageActual = command.getStorageActual();
            this.totalSales = command.getSales();
            this.lowestPrice = command.getPrice();
        }
    }


    private Integer calcTotalSales(Product productDetail) {
        return productDetail.getProductSkuList().stream().map(ProductSku::getSales).reduce(0, Integer::sum);
    }

    private BigDecimal findLowestPrice(Product productDetail) {
        ProductSku productSku = productDetail.getProductSkuList().stream().min(Comparator.comparing(ProductSku::getPrice)).orElseThrow(NoLowestPriceFoundException::new);
        return productSku.getPrice();
    }


}
