package kr.irm.fhir;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

import kr.irm.fhir.util.MyResponseHandler;
import kr.irm.fhir.util.URIBuilder;
import org.apache.commons.cli.*;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MHDBinaryRetrieve extends UtilContext {
	private static final Logger LOG = LoggerFactory.getLogger(MHDBinaryRetrieve.class);

	public static void main(String[] args) {
		LOG.info("starting mhd binary retrieve...");
		LOG.info("option args:{} ", Arrays.toString(args));
		Options opts = new Options();
		Map<String, Object> optionMap = new HashMap<String, Object>();
		setOptions(opts);

		// parse options
		if (parseOptions(optionMap, opts, args)) {
			LOG.error("mhd binary retrieve failed: invalid options");
			System.exit(1);
		}

		doSearch(optionMap);
	}

	private static void setOptions(Options opts) {
		// help
		opts.addOption("h", "help", false, "help");

		// Commons
		opts.addOption("o", OPTION_OAUTH_TOKEN, true, "OAuth Token");
		opts.addOption("s", OPTION_SERVER_URL, true, "FHIR Server Base URL");
		opts.addOption("du", OPTION_DOCUMENT_UUID, true, "Document UUID");
	}

	private static boolean parseOptions(Map<String, Object> optionMap, Options opts, String[] args) {
		boolean error = false;
		CommandLineParser parser = new DefaultParser();

		try {
			CommandLine cl = parser.parse(opts, args);

			// HELP
			if (cl.hasOption("h") || args.length == 0) {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp(
						"MHDBinaryRetrieve.sh [options]",
						"\nRetrieve Document Binary from MHD DocumentRecipient", opts,
						"Examples: $ ./MHDBinaryRetrieve.sh --document-status ...");
				System.exit(2);
			}

			// OAuth token (Required)
			if (cl.hasOption(OPTION_OAUTH_TOKEN)) {
				String oauth_token = cl.getOptionValue(OPTION_OAUTH_TOKEN);
				LOG.info("option {}={}", OPTION_OAUTH_TOKEN, oauth_token);

				optionMap.put(OPTION_OAUTH_TOKEN, oauth_token);
			}

			// FHIR
			// Server-url (Required)
			if (cl.hasOption(OPTION_SERVER_URL)) {
				String server_url = cl.getOptionValue(OPTION_SERVER_URL);
				LOG.info("option {}={}", OPTION_SERVER_URL, server_url);

				optionMap.put(OPTION_SERVER_URL, server_url);
			} else {
				error = true;
				LOG.error("option required: {}", OPTION_SERVER_URL);
			}

			// Document UUID
			if (cl.hasOption(OPTION_DOCUMENT_UUID)) {
				String documentUuid = cl.getOptionValue(OPTION_DOCUMENT_UUID);
				LOG.info("option {}={}", OPTION_DOCUMENT_UUID, documentUuid);

				optionMap.put(OPTION_DOCUMENT_UUID, documentUuid);
			} else {
				error = true;
				LOG.error("option required: {}", OPTION_DOCUMENT_UUID);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return error;
	}

	private static String doSearch(Map<String, Object> optionMap) {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		String httpResult = "";
		try {
			URIBuilder uriBuilder = new URIBuilder((String) optionMap.get(OPTION_SERVER_URL));
			uriBuilder.addPath("Binary");
			uriBuilder.addPath((String) optionMap.get(OPTION_DOCUMENT_UUID));

			String searchUrl = uriBuilder.build().toString();
			LOG.info("search url : {}", searchUrl);

			HttpGet httpGet = new HttpGet(searchUrl);
			httpGet.setHeader("Authorization", "Bearer " + optionMap.get(OPTION_OAUTH_TOKEN));

			ResponseHandler<String> responseHandler = new MyResponseHandler();
			httpResult = httpClient.execute(httpGet, responseHandler);
			LOG.info("Response : \n{}", httpResult);
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		} finally {
			try {
				if (httpClient != null) httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return httpResult;
	}

	private static List<String> getComponentList(String[] component) {
		List<String> componentList = new ArrayList<>();
		for (String s : component) {
			componentList.add(s);
		}

		return componentList;
	}
}
