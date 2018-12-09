package com.developer.springOracle3.controller;

import com.developer.springOracle3.annotation.ControllerViewName;
import com.developer.springOracle3.entity.CPtable;
import com.developer.springOracle3.entity.Customer;
import com.developer.springOracle3.model.repository.CPRepo;
import com.developer.springOracle3.model.repository.CustomerRepo;
import com.developer.springOracle3.model.repository.ProductionRepo;
import com.developer.springOracle3.model.service.CPService;
import com.developer.springOracle3.model.service.CustomerService;
import com.developer.springOracle3.model.service.ProductionService;
import com.developer.springOracle3.util.FDate;
import com.developer.springOracle3.util.MyException2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.text.ParseException;
import java.util.Optional;

@RestController
@ControllerViewName("indexpage")
@RequestMapping("/cp")
public class CPController {

    @Autowired
    private CustomerService customerService;
    @Autowired
    private CustomerRepo customerRepo;
    @Autowired
    private ProductionService prService;
    @Autowired
    private ProductionRepo prRepo;
    @Autowired
    private CPRepo cpRepo;

    @Autowired
    private CPService cpService;


    /**
     * @param cuid this one
     * @return
     * @throws ParseException
     */
    @RequestMapping(value = "/showCustomer", method = RequestMethod.POST)
    public ModelAndView showCu(@RequestParam("cuid") String cuid) throws ParseException {
        ModelAndView mv = new ModelAndView("indexpage");
        mv.addObject("listcu", customerRepo.findByCuid(cuid));
        mv.addObject("listbycu", cpService.findByCuid(cuid));
        return mv;
    }

    @RequestMapping("/showproduction")
    public ModelAndView showPr(@RequestParam("cuid") String cuid, @RequestParam("prid") String prid) throws ParseException {
        ModelAndView mv = new ModelAndView("indexpage");
        mv.addObject("liremain", prRepo.remainMeter(prid));
        mv.addObject("listcu", customerRepo.findByCuid(cuid));
        mv.addObject("listpr", prRepo.findByPrid(prid));
        mv.addObject("listbycu", cpService.findByCuid(cuid));

        return mv;
    }

    @RequestMapping("/saveCP")
    public ModelAndView saveCu(@ModelAttribute CPtable cPtable) throws MyException2, ParseException {

        ModelAndView mv = new ModelAndView("indexpage");
        if (cPtable.getId() != null) {

            if (cPtable.getFactore().equals("0")) {

                Customer customer = customerRepo.findByCuid(cPtable.getCuid());
                customer.setMande(customer.getMande() - Integer.parseInt(cPtable.getPay()));
                cPtable.setKaladate(FDate.farsi_to_grogerian(cPtable.getKaladate()));
                customerRepo.save(customer);
                cPtable.setRemain("0");
                cpRepo.save(cPtable);

            }
            else {
                CPtable cPtable1 = cpRepo.findById(cPtable.getId()).get();
                cPtable1.setPrid(cPtable1.getPrid());
                cPtable1.setPrName(cPtable1.getPrName());
                cPtable1.setRemain(cPtable.getRemain());
                cPtable1.setPrice(cPtable.getPrice());
                cPtable1.setFactore(cPtable.getFactore());
                cPtable1.setMetercp(cPtable.getMetercp());
                cPtable1.setPay(cPtable.getPay());
                cPtable1.setDiscount(cPtable.getDiscount());
                cPtable1.setKaladate(cPtable.getKaladate());

                cpService.save(cPtable1);
            }

        } else {

            cpService.save(cPtable);
        }

        mv.addObject("listcu", customerRepo.findByCuid(cPtable.getCuid()));
        mv.addObject("listbycu", cpService.findByCuid(cPtable.getCuid()));
        return mv;
    }

    @RequestMapping("/showCPtable")
    public ModelAndView saveCu() {
        ModelAndView mv = new ModelAndView("indexpage");
        mv.addObject("listcp", cpRepo.findAll());
        return mv;
    }

    @RequestMapping(value = "/deletCP/{id}", method = RequestMethod.GET)
    public ModelAndView delet(@PathVariable("id") int id) throws ParseException {
        ModelAndView mv = new ModelAndView("indexpage");
        Optional<CPtable> cPtable = cpRepo.findById(id);
        cpService.deletById(id);

        CPtable cPtable1 = cPtable.get();
        mv.addObject("listcu", customerRepo.findByCuid(cPtable1.getCuid()));
        mv.addObject("listbycu", cpService.findByCuid(cPtable1.getCuid()));
        return mv;
    }

    @RequestMapping(value = "/editcp/", method = RequestMethod.POST)
    public ModelAndView edited(@ModelAttribute CPtable cPtable) throws ParseException {
        ModelAndView mv = new ModelAndView("indexpage");
        Customer customer = customerRepo.findByCuid(cPtable.getCuid());
        if (cPtable.getFactore().equals("0"))
        {
            customer.setMande(customer.getMande()+Integer.parseInt(cPtable.getPay()));
        }else {
            customer.setMande(customer.getMande() - Integer.parseInt(cPtable.getRemain()));
        }
        customerRepo.save(customer);
        mv.addObject("edit", cpRepo.findById(cPtable.getId()).get());
        mv.addObject("listcu", customerRepo.findByCuid(cPtable.getCuid()));
        mv.addObject("listbycu", cpService.findByCuid(cPtable.getCuid()));
        return mv;

    }


}
