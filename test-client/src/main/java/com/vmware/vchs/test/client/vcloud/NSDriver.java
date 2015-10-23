package com.vmware.vchs.test.client.vcloud;/*
 * ******************************************************
 * Copyright VMware, Inc. 2014.   All Rights Reserved.
 * ******************************************************
 */

import com.vmware.vchs.networkservice.*;
import com.vmware.vcloud.api.rest.schema.LinkType;
import com.vmware.vcloud.api.rest.schema.SessionType;
import com.vmware.vcloud.api.rest.schema.TaskType;

import javax.xml.bind.*;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class NSDriver {

    public static void main1(final String[] args) throws JAXBException {
        final ObjectFactory factory = new ObjectFactory();

        final ExternalIpAddressAllocationType bar = factory.createExternalIpAddressAllocationType();
        bar.setExternalNetworkName("XAAS_");
        bar.setNumberOfExternalIpAddressesToAllocate(2);
        final ExternalIpAddressActionListType foo = factory.createExternalIpAddressActionListType();
        foo.getAllocation().add(bar);

        final JAXBElement<ExternalIpAddressActionListType> nook = factory.createExternalIpAddressActionList(foo);

//        System.out.println(nook.toString());

        final JAXBContext jc = JAXBContext.newInstance(ExternalIpAddressActionListType.class);
        final Marshaller marshaller = jc.createMarshaller();

        marshaller.marshal(nook, System.out);

    }

    public static void main2(final String[] args) throws JAXBException {
        final ObjectFactory factory = new ObjectFactory();

        final ExternalIpAddressDeallocationType bar = factory.createExternalIpAddressDeallocationType();
        bar.setExternalNetworkName("XAAS_");
        bar.getExternalIpAddress().add("100.64.222.106");
        final ExternalIpAddressActionListType foo = factory.createExternalIpAddressActionListType();
        foo.getDeallocation().add(bar);

        final JAXBElement<ExternalIpAddressActionListType> nook = factory.createExternalIpAddressActionList(foo);

//        System.out.println(nook.toString());

        final JAXBContext jc = JAXBContext.newInstance(ExternalIpAddressActionListType.class);
        final Marshaller marshaller = jc.createMarshaller();

        marshaller.marshal(nook, System.out);

    }

    public static void main3(final String[] args) throws JAXBException {


        final JAXBContext jc = JAXBContext.newInstance(SessionType.class);
        final Unmarshaller unmarshaller = jc.createUnmarshaller();

        try (final FileInputStream is = new FileInputStream("/Users/tdas/foobar.xml")) {
            final Source source = new StreamSource(is);
            final JAXBElement<SessionType> obj = unmarshaller.unmarshal(source, SessionType.class);
            final SessionType foobar = obj.getValue();
            System.out.println(foobar.getHref());
            final List<LinkType> links = foobar.getLink();
            for (final LinkType link : links) {
                System.out.println("-------------------------");
                System.out.println(link.getHref());
                System.out.println(link.getId());
                System.out.println(link.getName());
                System.out.println(link.getRel());
                System.out.println(link.getType());

            }
            System.out.println(obj.toString());
        } catch (final FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void main4(final String[] args) throws JAXBException {

        final JAXBContext jc = JAXBContext.newInstance(EdgeGatewayExternalConnectionListType.class);
        final Unmarshaller unmarshaller = jc.createUnmarshaller();

        try (final FileInputStream is = new FileInputStream("/Users/tdas/egconnlist.xml")) {
            final Source source = new StreamSource(is);
            final JAXBElement<EdgeGatewayExternalConnectionListType> obj = unmarshaller.unmarshal(source, EdgeGatewayExternalConnectionListType.class);
            //final QueryResultRecordsType queryResults = JAXB.unmarshal(is, QueryResultRecordsType.class);
            final EdgeGatewayExternalConnectionListType queryResults = obj.getValue();
            System.out.println(queryResults);
            final List<EdgeGatewayExternalConnectionType> connlist = queryResults.getEdgeGatewayExternalConnection();
            for (final EdgeGatewayExternalConnectionType conn : connlist) {
                System.out.println(conn.getExternalNetworkName());
                System.out.println(conn.getIpAddress());
                System.out.println(conn.isIsUsedInRule());
            }


        } catch (final FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void main(final String[] args) throws JAXBException {

        final JAXBContext jc = JAXBContext.newInstance(TaskType.class);
        final Unmarshaller unmarshaller = jc.createUnmarshaller();

        try (final FileInputStream is = new FileInputStream("/Users/tdas/task.xml")) {
            final Source source = new StreamSource(is);
            final JAXBElement<TaskType> obj = unmarshaller.unmarshal(source, TaskType.class);
            //final QueryResultRecordsType queryResults = JAXB.unmarshal(is, QueryResultRecordsType.class);
            final TaskType queryResults = obj.getValue();
            System.out.println(queryResults);
            System.out.println(queryResults.getOperation());
            System.out.println(queryResults.getDescription());
            System.out.println(queryResults.getDetails());
            System.out.println(queryResults.getOperationKey());
            System.out.println(queryResults.getStatus());
            System.out.println(queryResults.getType());
            System.out.println(queryResults.getLink());
            System.out.println(queryResults.getHref());




        } catch (final FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
