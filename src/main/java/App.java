import java.util.Map;
import java.util.HashMap;
import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;
import static spark.Spark.*;
import java.util.List;
import java.util.ArrayList;


public class App {
  public static void main(String[] args) {
    staticFileLocation("/public");
    String layout = "templates/layout.vtl";

    get("/", (request, response) -> {
      Map<String, Object> model = new HashMap<String, Object>();
      List<Integer> goodsIds = request.session().attribute("cart");
      List<Good> goods = new ArrayList<>();
      if(goodsIds != null) {
        for(int id : goodsIds) {
          goods.add(Good.findById(id));
        }
      }
      model.put("cart", goods);
      model.put("loggedInUserId", User.getLoggedInUserId());
      model.put("coffees",Good.allByType("coffee"));
      model.put("breads",Good.allByType("bread"));
      model.put("sandwiches",Good.allByType("sandwich"));
      model.put("template", "templates/index.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    get("/goods/new", (request, response) -> {
      Map<String, Object> model = new HashMap<String, Object>();
      model.put("template", "templates/goods-new.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    post("/goods/new", (request, response) -> {
      Map<String, Object> model = new HashMap<String, Object>();
      String newName = request.queryParams("name");
      int newStockPrice = Integer.parseInt(request.queryParams("stock"));
      int newSellPrice = Integer.parseInt(request.queryParams("sell"));
      String newType = request.queryParams("type");
      Good newGood = new Good(newName, newStockPrice, newSellPrice, newType);
      newGood.save();
      response.redirect("/");
      return new ModelAndView(model,layout);
    }, new VelocityTemplateEngine());

    get("/goods/:id", (request,response) -> {
      Map<String, Object> model = new HashMap<String, Object>();
      int temp = Integer.parseInt(request.params(":id"));
      List<Integer> goodsIds = request.session().attribute("cart");
      if (goodsIds == null) {
        goodsIds = new ArrayList<>();
        goodsIds.add(temp);
        request.session().attribute("cart", goodsIds);
        response.redirect("/");
      } else if (goodsIds.size() >= 0) {
        goodsIds.add(temp);
        response.redirect("/");
      } else {
        response.redirect("/error");
      }

      return new ModelAndView(model,layout);
    }, new VelocityTemplateEngine());

    get("/cart/checkout", (request,response) -> {
      Map<String, Object> model = new HashMap<String, Object>();
      List<Integer> goodsIds = request.session().attribute("cart");
      List<Good> goodsCart = new ArrayList<>();
      int salePrice = 0;
      for (Integer item : goodsIds ) {
        Good newGood = Good.findById(item);
        goodsCart.add(newGood);
        salePrice += newGood.getSellPrice();
      }
      model.put("salePrice", salePrice);
      model.put("cart",goodsCart);
      model.put("template", "templates/checkout.vtl");
      return new ModelAndView(model,layout);
    }, new VelocityTemplateEngine());

    post("/cart/checkout", (request,response) -> {
      Map<String, Object> model = new HashMap<String, Object>();
      List<Integer> goodsIds = request.session().attribute("cart");
      request.session().attribute("cart", null);
      List<Good> goodsList = new ArrayList<>();
      // User.getLoggedInUser().getId()
      Transaction newTransaction = new Transaction(User.getLoggedInUserId());
      newTransaction.save();
      for (Integer item : goodsIds ) {
        goodsList.add(Good.findById(item));
        newTransaction.linkTransactionGoods(item);
      }
      model.put("totalCost", newTransaction.getSalePrice());
      model.put("goods", goodsList);
      model.put("template", "templates/invoice.vtl");
      return new ModelAndView(model,layout);
    }, new VelocityTemplateEngine());

    get("/cart/:id", (request,response) -> {
      Map<String, Object> model = new HashMap<String, Object>();
      List<Integer> goodsIds = request.session().attribute("cart");
      int removeIndex = Integer.parseInt(request.params(":id"));
      goodsIds.remove(removeIndex);
      response.redirect("/cart/checkout");
      return new ModelAndView(model,layout);
    }, new VelocityTemplateEngine());

    get("/sign-in", (request,response) -> {
      Map<String, Object> model = new HashMap<String, Object>();
      model.put("template", "templates/sign-in.vtl");
      return new ModelAndView(model,layout);
    }, new VelocityTemplateEngine());

    post("/sign-in", (request,response) -> {
      Map<String, Object> model = new HashMap<String, Object>();
      String userName = request.queryParams("user-name");
      String password = request.queryParams("password");
      if (!userName.equals("") && !password.equals("")) {
        if (User.userValidated(userName, password) > 0) {
          User.loggedInUserId = User.userValidated(userName, password);
          response.redirect("/");
        }
      }
      model.put("invalidLogIn", true);
      model.put("template", "templates/sign-in.vtl");
      return new ModelAndView(model,layout);
    }, new VelocityTemplateEngine());

    post("/new-account", (request,response) -> {
      Map<String, Object> model = new HashMap<String, Object>();
      String userName = request.queryParams("create-user-name");
      String password = request.queryParams("create-password");
      if (!userName.equals("") && !password.equals("")) {
        if (!User.userNameExists(userName)) {
          User newUser = new User(userName, password);
          newUser.save();
          User.loggedInUserId = newUser.getId();
          response.redirect("/");
        }
      }
      model.put("invalidLogIn", true);
      model.put("template", "templates/sign-in.vtl");
      return new ModelAndView(model,layout);
    }, new VelocityTemplateEngine());

    get("/sign-out", (request,response) -> {
      Map<String, Object> model = new HashMap<String, Object>();
      User.loggedInUserId = -1;
      request.session().attribute("cart", null);
      response.redirect("/");
      model.put("template", "templates/sign-in.vtl");
      return new ModelAndView(model,layout);
    }, new VelocityTemplateEngine());

    get("/user-data", (request,response) -> {
      Map<String, Object> model = new HashMap<String, Object>();
      model.put("users", User.allUsers());
      model.put("template", "templates/user-data.vtl");
      return new ModelAndView(model,layout);
    }, new VelocityTemplateEngine());

    get("/user/:id", (request,response) -> {
      Map<String, Object> model = new HashMap<String, Object>();
      int userId = Integer.parseInt(request.params(":id"));
      model.put("user", User.findById(userId));
      model.put("template", "templates/user.vtl");
      return new ModelAndView(model,layout);
    }, new VelocityTemplateEngine());

    get("/sales-report", (request,response) -> {
      Map<String, Object> model = new HashMap<String, Object>();
      model.put("goods", Good.all());
      model.put("template", "templates/sales-report.vtl");
      return new ModelAndView(model,layout);
    }, new VelocityTemplateEngine());

    get("/transaction/:id", (request,response) -> {
      Map<String, Object> model = new HashMap<String, Object>();
      int transactionId = Integer.parseInt(request.params(":id"));
      Transaction transaction = Transaction.findById(transactionId);
      model.put("transaction",transaction);
      model.put("goods", transaction.getGoodsIds());
      model.put("Good", Good.class);
      model.put("template", "templates/sales-report.vtl");
      return new ModelAndView(model,layout);
    }, new VelocityTemplateEngine());



  }

}
