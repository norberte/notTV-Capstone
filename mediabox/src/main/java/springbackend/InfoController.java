package springbackend;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import springbackend.view.CategoryType;
import springbackend.view.CategoryValue;

/**
 * Responsible for serving data from the database.
 *
 * @author
 */
@Controller
public class InfoController {

    @GetMapping("/info/categories")
    @ResponseBody
    public CategoryType[] getCategories() {
	// test data.
	List<CategoryValue> misc = Arrays.asList(
	    new CategoryValue(1, "In Library"),
	    new CategoryValue(2, "Short Videos"),
	    new CategoryValue(3, "Long Videos")
	);
	List<CategoryValue> city = Arrays.asList(
	    new CategoryValue(1, "Edmonton"),
	    new CategoryValue(2, "Kelowna")
	); 
	List<CategoryType> categories = Arrays.asList(
	    new CategoryType("Misc", misc.toArray(new CategoryValue[misc.size()])),
	    new CategoryType("City", city.toArray(new CategoryValue[city.size()]))
	);
	return categories.toArray(new CategoryType[categories.size()]);
    }
}
