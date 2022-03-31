package org.camunda.community.zeebe.play.rest

import io.camunda.zeebe.client.ZeebeClient
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/rest/deployments")
class DeploymentsResource(private val zeebeClient: ZeebeClient) {

    @RequestMapping(path = ["/"], method = [RequestMethod.POST])
    fun deployResources(@RequestParam("resources") resources: Array<MultipartFile>): Long {

        if (resources.isEmpty()) {
            throw RuntimeException("no resources to deploy")
        }

        val firstResource = resources.first()

        val deployCommand = zeebeClient.newDeployCommand()
            .addResourceBytes(firstResource.bytes, firstResource.name)

        resources
            .drop(1)
            .forEach {
                deployCommand.addResourceBytes(it.bytes, it.name)
            }

        return deployCommand
            .send()
            .join()
            .key;
    }

}