package com.test.mongodb.controller


import com.test.mongodb.model.Customer
import com.test.mongodb.model.Product
import com.test.mongodb.repo.CustomerRepository
import com.test.mongodb.repo.ProductRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.StringJoiner
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.set


@RestController
@RequestMapping(value = ["/api"])
class MongoDBRestController {



    //Create Two Table........


    @Autowired
    internal var customerRepository: CustomerRepository? = null




    //First Table

    @Autowired
    internal var productRepository: ProductRepository? = null


    @GetMapping("/productCreate")
    fun productCreate():List<Product>{
        return  productRepository!!.findAll()
    }

     @PostMapping("/productCreate")
     fun productCreate(@RequestBody product: Product):Any{

         val id=productRepository!!.existsById(product.procID)
         if(!id) { productRepository!!.save(product)
             return ResponseEntity(responseMessage(true,"Product is  create successfully."),HttpStatus.CREATED)
         }else
             return ResponseEntity(responseMessage(false,"This Product already exist !!."),HttpStatus.CONFLICT)
     }



    //2nd Table

    /* Simple Customer GetData*/

    @GetMapping(value = ["/customer"])
    fun getData(): MutableList<Customer>{

      val s= customerRepository!!.findAll()

        val li=ArrayList<Customer>()
        for(d in s){
           val ss=  "http://localhost:8080/api/images/"+d.avatar
            li.add(Customer(d.id,d.name,d.location,ss))
        }
        return li
    }



    //Create Customer
    @PostMapping("/customer")
    private fun createCustomer(@RequestBody customer: Customer): Any {

        val id=customerRepository!!.existsById(customer.id)
        if(!id) { customerRepository!!.save(customer)
            return ResponseEntity(responseMessage(true,"Customer is  create successfully."),HttpStatus.CREATED)
        }else
        return ResponseEntity(responseMessage(false,"This customer already exist !!."),HttpStatus.CONFLICT)
    }







    @GetMapping("/customer/{location}")
      fun findByLocation(@PathVariable("location") location:String):Customer{
        return customerRepository!!.findByLocation(location)
    }


    @DeleteMapping("/customer")
       fun deleteCustomer():Any{
        customerRepository!!.deleteAll()
        return responseMessage(true,"Customer are deleted successfully.")
    }




    //create profile
    private val UPLOADED_FOLDER = "E:\\Spring\\api\\mongodb\\imagesUpload\\"
    @PostMapping("/upload",consumes = arrayOf("multipart/form-data"))
    fun singleFileUpload(@RequestParam("file") file: MultipartFile,
                         @RequestParam("id") id :String,
                         @RequestParam("name") name :String,
                         @RequestParam("location") location :String): Any {
        if (file.isEmpty) {
            return ResponseEntity(responseMessage(false,"Please select a file to upload."),HttpStatus.NOT_FOUND)
        }

        val avatarPath=file.originalFilename
        try {
            val bytes = file.bytes
            val path = Paths.get(UPLOADED_FOLDER + avatarPath)
            Files.write(path, bytes)
           // customerRepository!!.save(Customer(id,name,location,"http://localhost:8080/api/images/"+avatarPath!!))
            customerRepository!!.save(Customer(id,name,location,avatarPath!!))
            return responseMessage(true,"Customer is uploaded successfully. ${path.toAbsolutePath()}")
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return ""
    }



    // uploadMultiple File
    @PostMapping("/uploadMulti")
    fun multiFileUpload(@RequestParam("files") files: Array<MultipartFile>, redirectAttributes: RedirectAttributes): String {

        val sj = StringJoiner(" , ")

        for (file in files) {

            if (file.isEmpty) {
                continue //next pls
            }

            try {

                val bytes = file.bytes
                val path = Paths.get(UPLOADED_FOLDER + file.originalFilename)
                Files.write(path, bytes)

                sj.add(file.originalFilename)

            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

        val uploadedFileName = sj.toString()
        if (StringUtils.isEmpty(uploadedFileName)) {
            redirectAttributes.addFlashAttribute("message",
                    "Please select a file to upload")
        } else {
            redirectAttributes.addFlashAttribute("message",
                    "You successfully uploaded '$uploadedFileName'")
        }

        return "redirect:/uploadStatus"

    }

    @GetMapping("/images/{path}")
    @ResponseBody
    @Throws(IOException::class)
    fun getPhoto(@PathVariable ("path") path:String): ResponseEntity<ByteArray> {
        val imgPath = File("E:\\\\temp\\\\$path")

        val image = Files.readAllBytes(imgPath.toPath())
        val headers = HttpHeaders()
        headers.contentType = MediaType.IMAGE_JPEG
        headers.contentLength = image.size.toLong()
        return ResponseEntity(image, headers, HttpStatus.OK)
    }




    private fun responseMessage(s:Boolean,mess:String):Any{
        val map=HashMap<String,Any>()
        map["status"]=s
        map["message"]=mess
        return map
    }


}
