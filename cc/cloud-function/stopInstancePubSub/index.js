const Compute = require('@google-cloud/compute')
const compute = new Compute()

exports.stopInstancePubSub = async(event, context, callback) = > {
    try {
        const payload = _validatePayload(
            JSON.parse(Buffer.from(event.data, 'base64').toString())
        )
        const options = { filter: `labels.${payload.label}` }
        const[vms] = await compute.getVMs(options)
        await Promise.all(
            vms.map(async (instance) => {
                console.log(instance.zone.id)
                console.log(instance.name)
                const [operation] = await compute
                    .zone(instance.zone.id)
                    .vm(instance.name)
                    .stop()
                // Operation pending
                return operation.promise()
            })
        )

        // Operation complete. Instance successfully stopped.
        const message = `Successfully stopped instance(s)`
        console.log(message)
        callback(null, message)
    } catch(err) {
        console.log(err)
        callback(err)
    }
}

const _validatePayload = (payload) = > {
    if(!payload.label) {
        throw new Error(`Attribute 'label' missing from payload`)
    }
return payload
}
